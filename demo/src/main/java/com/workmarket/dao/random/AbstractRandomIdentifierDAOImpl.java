package com.workmarket.dao.random;

import com.workmarket.dao.exception.random.IdentifierGenerationException;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.RandomUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractRandomIdentifierDAOImpl implements AbstractRandomIdentifierDAO {

	public AbstractRandomIdentifierDAOImpl(final String tableName, final String uniqueColumnName, final int uniqueColumnNumberLength, final JdbcTemplate jdbcTemplate) {
		this.tableName = checkNotNull(tableName);
		this.uniqueColumnName = checkNotNull(uniqueColumnName);
		this.uniqueColumnNumberLength = checkNotNull(uniqueColumnNumberLength);
		checkArgument(uniqueColumnNumberLength < 15);//we don't support more than 15 wide unique numbers
		this.jdbcTemplate = checkNotNull(jdbcTemplate);
		this.highestPossibleNumber =  (long)Math.pow(10d, uniqueColumnNumberLength) - 1L;
		this.multiplier = (long)Math.pow(10d, uniqueColumnNumberLength - 1);
		this.sql =
			" SELECT " + uniqueColumnName + " FROM " + tableName +
			" WHERE " + uniqueColumnName + " = ?";
	}

	private static final Log logger = LogFactory.getLog(AbstractRandomIdentifierDAOImpl.class);
	protected final String tableName;
	protected final String uniqueColumnName;
	private final String sql;
	protected final int uniqueColumnNumberLength;
	protected final long highestPossibleNumber;
	protected final long multiplier;
	protected final JdbcTemplate jdbcTemplate;
	protected final SecureRandom secureRandom = new SecureRandom();

	@Override
	public int getNumberLength() {
		return uniqueColumnNumberLength;
	}

	@Override
	public String generateUniqueNumber() throws IdentifierGenerationException {
		Long newNumber = generateRandomNumber();
		int limit = 20; // to protect against infinite while loop
		while (newNumber == null && limit > 0) {
			newNumber = generateRandomNumber();
			limit--;
		}
		if (newNumber == null) {
			throw new IdentifierGenerationException("Threshold hit for maximum number of id generation attempts. Database may be running out of " + uniqueColumnName + ".");
		}
		return newNumber.toString();
	}

	protected Long generateRandomNumber() {
		final Long nextNumber =
				ensureNumberLength(
						nextLong(secureRandom, highestPossibleNumber) + 1L
				);

		PreparedStatementSetter preparedStatementSetter =
			new PreparedStatementSetter() {
				@Override public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, "" + nextNumber);
				}
			};

		ResultSetExtractor resultSetExtractor =
			new ResultSetExtractor<Object>() {
				@Override public Object extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					if (resultSet.next()) { return resultSet.getString(1); }
					return null;
				}
			};

		try {
			Object foundObj = jdbcTemplate.query(sql, preparedStatementSetter, resultSetExtractor);
			if (foundObj == null) {
				return nextNumber;
			}
			logger.debug(String.format("Duplicate identifier %d generated for %s", nextNumber, uniqueColumnName));
			return null;
		} catch (DataAccessException e) {
			logger.info(String.format("Error: %s, for identifier %d generated for %s", e.getMessage(), nextNumber, uniqueColumnName));
			return null;
		}
	}

	// implementation of nextLong with range argument
	private long nextLong(Random rng, long n) {
		// error checking and 2^x checking removed for simplicity.
		// see http://stackoverflow.com/questions/2546078/java-random-long-number-in-0-x-n-range
		long bits, val;
		do {
			bits = (rng.nextLong() << 1) >>> 1;
			val = bits % n;
		} while (bits-val+(n-1) < 0L);
		return val;
	}

	private long ensureNumberLength(long n) {
		if ( NumberUtilities.getLength(n) < uniqueColumnNumberLength ) {
			n = n + RandomUtilities.nextIntInRange(1, 9) * multiplier;
		}
		return n;
	}
}
