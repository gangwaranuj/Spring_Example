import React, {
  PureComponent
} from 'react';
// Re-Enable for Dev Env functionality.
// import injectTapEventPlugin from 'react-tap-event-plugin';
import { Map } from 'immutable';
import { WMMessageBanner } from '@workmarket/front-end-components';
import {
  createValidation
} from '../utils';
import {
  adjustStartDate,
  disabledRangeObj
} from '../utils/visuals';
import myProps from './PropTypes';
import ValidityBanner from './subcomponents/ValidityBanner';
import RecurrenceRadio from './subcomponents/RecurrenceRadio';
import FrequencyDropDown from './subcomponents/FrequencyDropDown';
import PopulateFrequencyOption from './subcomponents/PopulateFrequencyOption';

const recurDescription = 'Repeating assignments will create drafts that occur on a daily, weekly, or monthly basis.';

/*
Re-Enable for Dev Env functionality.
if (process.env.NODE_ENV === 'development') {
	injectTapEventPlugin();
}
*/

class WMRecurrence extends PureComponent {

	constructor (props) {
		super(props);

		this.state = {
			validity: {
				valid: true,
				reason: 'This module has not been engaged yet, so I am valid.'
			}
		};
	}

	componentWillReceiveProps (nextProps) {
    // Because these come from a seperate source of reduction entirely, I run
    // secondary logic on these to only re-render and change state if they are
    // different then before.
		if (nextProps.foreignStartDate !== this.props.foreignStartDate) {
			this.props.handleStartDate(adjustStartDate(nextProps.foreignStartDate));
		}
		if (nextProps.foreignRange === true) {
			this.props.handleRecurrence('Single');
		}
    // Not every state change affects how I display the status of this repetition
    // to the user, so I first stringify this status to see if its different before
    // I update it.
		const newValidity = createValidation(nextProps);
		if (newValidity.reason !== this.props.validity.reason) {
			this.props.handleValidity(Map(newValidity));
		}
	}

	render () {
		const {
      type,
      frequency,
      startDate,
      foreignRange,
      validity,
      frequencyModifier,
      handleRecurrence,
      handleFrequency,
      handleFrequencyModifier,
      errors,
      initialError
    } = this.props;

		// This switches between the different types of options recurring assignments
    // currentely allow.
		const maxFrequency = (frequency === 'Daily' || frequency === 'Weekly') ? 7 : 4;
		const CurrentOption = PopulateFrequencyOption(this.props);
		
		return (
			<div>
				<div style={ { paddingBottom: '1em' } }>
					<span>
						{ recurDescription }
					</span>
				</div>
				<RecurrenceRadio
					handleRecurrence={ handleRecurrence }
					type={ type }
					foreignRange={ foreignRange }
					startDate={ startDate }
				/>
				{
          initialError &&
          <WMMessageBanner 
          	status="warning" 
            hideDismiss
          >
            <div style={ { paddingLeft: '1em' } }>
            	<span>{ initialError }</span>
  					</div>
          </WMMessageBanner>
        }
				{
          type === 'Recur' &&
          <div>
          	<FrequencyDropDown
          		frequency={ frequency }
          		handleFrequency={ handleFrequency }
          		maxFrequency={ maxFrequency }
          		handleFrequencyModifier={ handleFrequencyModifier }
          		frequencyModifier={ frequencyModifier }
          	/>
          	<div style={ { paddingBottom: '0.5em' } }>
          		<div style={ { paddingBottom: '1em' } }>
          			{ CurrentOption }
          		</div>
							{
                errors &&
                errors.length > 0 &&
                <WMMessageBanner status="warning" hideDismiss>
									{ errors.map((error, i) => 
                    <div 
                    	key={ i } 
                      style={ { paddingLeft: '1em' } }
                    >
                    	<span>{ error }</span>
                  	</div>) 
                	}
                </WMMessageBanner>
              }
							<ValidityBanner validity={ validity } errors={ errors } />
						</div>
          </div>
        }
				{
          foreignRange &&
          <ValidityBanner validity={ disabledRangeObj } />
        }
			</div>
		);
	}
}

WMRecurrence.propTypes = myProps;

export default WMRecurrence;
