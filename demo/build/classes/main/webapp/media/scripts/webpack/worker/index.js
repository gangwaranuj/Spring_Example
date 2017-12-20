'use strict';

import Application from '../core';
import workFeed from '../workfeed/main';

Application.init({ name: 'feed', features: config }, () => {});

workFeed.create(config);
