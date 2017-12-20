package com.workmarket.domains.model.screening;

import com.google.common.collect.ImmutableList;
import com.workmarket.screening.model.Screening;
import com.workmarket.screening.model.ScreeningStatusCode;
import com.workmarket.screening.model.VendorRequestCode;
import com.workmarket.service.business.screening.ScreeningAndUser;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Locale;

/**
 * Converts screening service DTOs to the object types the monolith already has.
 */
public class ScreeningObjectConverter {
    /**
     * Convert screen pagination to uservice kind.
     */
    public static List<Screening> convertPagination(final ScreeningPagination paginated) {
        final ImmutableList.Builder<Screening> result = ImmutableList.builder();

        for (final com.workmarket.domains.model.screening.Screening item: paginated.getResults()) {
            result.add(convertScreeningFromLegacy(item));
        }
        return result.build();
    }

    public static List<ScreeningAndUser> convertPaginationWithUsers(final ScreeningPagination paginated) {
        final ImmutableList.Builder<ScreeningAndUser> result = ImmutableList.builder();

        for (final com.workmarket.domains.model.screening.Screening item: paginated.getResults()) {
            result.add(new ScreeningAndUser(convertScreeningFromLegacy(item), item.getUser()));
        }
        return result.build();
    }

    /**
     * Convert a screening service response to monolith form.
     */
    public static Screening convertScreeningFromLegacy(
            final com.workmarket.domains.model.screening.Screening response) {
        if (response == null) {
            return null;
        }
        return Screening.builder()
            .setCreatedOn(new DateTime(response.getRequestDate()))
            .setIsDeleted(false)
            .setStatus(convertStatusCode(response.getScreeningStatusType()))
            .setVendorRequestCode(getVendorRequestCode(response))
            .setVendorResponseDate(response.getResponseDate() != null ? new DateTime(response.getResponseDate()) : null)
            .setUuid(response.getScreeningId())
            .setVendorRequestId(response.getScreeningId())
            .build();
    }

    private static VendorRequestCode getVendorRequestCode(
        final com.workmarket.domains.model.screening.Screening screening) {

        if (screening instanceof BackgroundCheck) {
            return VendorRequestCode.BACKGROUND;
        } else if (screening instanceof DrugTest) {
            return VendorRequestCode.DRUG;
        } else {
            throw new RuntimeException("unknown screening type " + screening.getClass());
        }
    }


    public static com.workmarket.domains.model.screening.Screening convertScreeningResponseToMonolith(
        final Screening response) {
        if (response == null) {
            return null;
        }

        com.workmarket.domains.model.screening.Screening result;

        if (VendorRequestCode.DRUG.equals(response.getVendorRequestCode())) {
            result = new DrugTest();
        } else if (VendorRequestCode.BACKGROUND.equals(response.getVendorRequestCode())) {
            result = new BackgroundCheck();
        } else {
            throw new RuntimeException("Unknown vendor request");
        }

        result.setRequestDate(response.getCreatedOn().toCalendar(Locale.US)); //! get locale from user
        result.setScreeningStatusType(convertScreeningStatusType(response.getStatus()));
        result.setScreeningId(response.getUuid());
        result.setResponseDate(response.getVendorResponseDate() != null
            ? response.getVendorResponseDate().toCalendar(Locale.US) : null);
        return result;
    }

    private static ScreeningStatusType convertScreeningStatusType(final ScreeningStatusCode status) {
        if (ScreeningStatusCode.REQUESTED.equals(status)) {
            return new ScreeningStatusType(ScreeningStatusType.REQUESTED);
        } else if (ScreeningStatusCode.ERROR.equals(status)) {
            return new ScreeningStatusType(ScreeningStatusType.ERROR);
        } else if (ScreeningStatusCode.PASSED.equals(status)) {
            return new ScreeningStatusType(ScreeningStatusType.PASSED);
        } else if (ScreeningStatusCode.FAILED.equals(status)) {
            return new ScreeningStatusType(ScreeningStatusType.FAILED);
        } else if (ScreeningStatusCode.NOREQUEST.equals(status)) {
            return new ScreeningStatusType(ScreeningStatusType.NOT_REQUESTED);
        } else if (ScreeningStatusCode.CANCELLED.equals(status)) {
            return new ScreeningStatusType(ScreeningStatusType.CANCELLED);
        } else if (ScreeningStatusCode.REVIEW.equals(status)) {
            return new ScreeningStatusType(ScreeningStatusType.REVIEW);
        } else if (ScreeningStatusCode.EXPIRED.equals(status)) {
            return new ScreeningStatusType(ScreeningStatusType.EXPIRED);
        } else {
            throw new UnsupportedOperationException("cannot convert status code " + status.code());
        }
    }

    /**
     * Convert a screening status code from legacy form.
     */
    public static ScreeningStatusCode convertStatusCode(final ScreeningStatusType screeningStatusType) {
        return convertStatusCode(screeningStatusType.getCode());
    }

    /**
     * Convert a screening status code from legacy code form.
     */
    public static ScreeningStatusCode convertStatusCode(final String statusCode) {
      switch (statusCode) {
          case ScreeningStatusType.REQUESTED:
              return ScreeningStatusCode.REQUESTED;
          case ScreeningStatusType.ERROR:
              return ScreeningStatusCode.ERROR;
          case ScreeningStatusType.PASSED:
              return ScreeningStatusCode.PASSED;
          case ScreeningStatusType.FAILED:
            return ScreeningStatusCode.FAILED;
          case ScreeningStatusType.NOT_REQUESTED:
              return ScreeningStatusCode.NOREQUEST;
          case ScreeningStatusType.CANCELLED:
              return ScreeningStatusCode.CANCELLED;
          case ScreeningStatusType.REVIEW:
              return ScreeningStatusCode.REVIEW;
          case ScreeningStatusType.EXPIRED:
              return ScreeningStatusCode.EXPIRED;
          default:
              throw new UnsupportedOperationException("cannot convert status code " + statusCode);
        }
    }

    public static com.workmarket.domains.model.screening.Screening convertToLegacyScreening(
        final ScreeningAndUser screening) {
        final com.workmarket.domains.model.screening.Screening newScreening = convertScreeningResponseToMonolith(
            screening.getScreening());
        newScreening.setUser(screening.getUser());
        return newScreening;
    }
}
