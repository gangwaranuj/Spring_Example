'use strict';

export default window.optimizely || { push: function () { return false; } };
