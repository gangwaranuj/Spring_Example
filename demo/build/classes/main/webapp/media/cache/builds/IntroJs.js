webpackJsonp([2],{

/***/ "./node_modules/intro.js/intro.js":
/***/ (function(module, exports, __webpack_require__) {

/**
 * Intro.js v0.9.0
 * https://github.com/usablica/intro.js
 * MIT licensed
 *
 * Copyright (C) 2013 usabli.ca - A weekend project by Afshin Mehrabani (@afshinmeh)
 */

(function (root, factory) {
  if (true) {
    // CommonJS
    factory(exports);
  } else if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['exports'], factory);
  } else {
    // Browser globals
    factory(root);
  }
} (this, function (exports) {
  //Default config/variables
  var VERSION = '0.9.0';

  /**
   * IntroJs main class
   *
   * @class IntroJs
   */
  function IntroJs(obj) {
    this._targetElement = obj;

    this._options = {
      /* Next button label in tooltip box */
      nextLabel: 'Next &rarr;',
      /* Previous button label in tooltip box */
      prevLabel: '&larr; Back',
      /* Skip button label in tooltip box */
      skipLabel: 'Skip',
      /* Done button label in tooltip box */
      doneLabel: 'Done',
      /* Default tooltip box position */
      tooltipPosition: 'bottom',
      /* Next CSS class for tooltip boxes */
      tooltipClass: '',
      /* Close introduction when pressing Escape button? */
      exitOnEsc: true,
      /* Close introduction when clicking on overlay layer? */
      exitOnOverlayClick: true,
      /* Show step numbers in introduction? */
      showStepNumbers: true,
      /* Let user use keyboard to navigate the tour? */
      keyboardNavigation: true,
      /* Show tour control buttons? */
      showButtons: true,
      /* Show tour bullets? */
      showBullets: true,
      /* Scroll to highlighted element? */
      scrollToElement: true,
      /* Set the overlay opacity */
      overlayOpacity: 0.8
    };
  }

  /**
   * Initiate a new introduction/guide from an element in the page
   *
   * @api private
   * @method _introForElement
   * @param {Object} targetElm
   * @returns {Boolean} Success or not?
   */
  function _introForElement(targetElm) {
    var introItems = [],
        self = this;

    if (this._options.steps) {
      //use steps passed programmatically
      var allIntroSteps = [];

      for (var i = 0, stepsLength = this._options.steps.length; i < stepsLength; i++) {
        var currentItem = _cloneObject(this._options.steps[i]);
        //set the step
        currentItem.step = introItems.length + 1;
        //use querySelector function only when developer used CSS selector
        if (typeof(currentItem.element) === 'string') {
          //grab the element with given selector from the page
          currentItem.element = document.querySelector(currentItem.element);
        }

        //intro without element
        if (typeof(currentItem.element) === 'undefined' || currentItem.element == null) {
          var floatingElementQuery = document.querySelector(".introjsFloatingElement");

          if (floatingElementQuery == null) {
            floatingElementQuery = document.createElement('div');
            floatingElementQuery.className = 'introjsFloatingElement';

            document.body.appendChild(floatingElementQuery);
          }

          currentItem.element  = floatingElementQuery;
          currentItem.position = 'floating';
        }

        if (currentItem.element != null) {
          introItems.push(currentItem);
        }
      }

    } else {
       //use steps from data-* annotations
      var allIntroSteps = targetElm.querySelectorAll('*[data-intro]');
      //if there's no element to intro
      if (allIntroSteps.length < 1) {
        return false;
      }

      //first add intro items with data-step
      for (var i = 0, elmsLength = allIntroSteps.length; i < elmsLength; i++) {
        var currentElement = allIntroSteps[i];
        var step = parseInt(currentElement.getAttribute('data-step'), 10);

        if (step > 0) {
          introItems[step - 1] = {
            element: currentElement,
            intro: currentElement.getAttribute('data-intro'),
            step: parseInt(currentElement.getAttribute('data-step'), 10),
            tooltipClass: currentElement.getAttribute('data-tooltipClass'),
            position: currentElement.getAttribute('data-position') || this._options.tooltipPosition
          };
        }
      }

      //next add intro items without data-step
      //todo: we need a cleanup here, two loops are redundant
      var nextStep = 0;
      for (var i = 0, elmsLength = allIntroSteps.length; i < elmsLength; i++) {
        var currentElement = allIntroSteps[i];

        if (currentElement.getAttribute('data-step') == null) {

          while (true) {
            if (typeof introItems[nextStep] == 'undefined') {
              break;
            } else {
              nextStep++;
            }
          }

          introItems[nextStep] = {
            element: currentElement,
            intro: currentElement.getAttribute('data-intro'),
            step: nextStep + 1,
            tooltipClass: currentElement.getAttribute('data-tooltipClass'),
            position: currentElement.getAttribute('data-position') || this._options.tooltipPosition
          };
        }
      }
    }

    //removing undefined/null elements
    var tempIntroItems = [];
    for (var z = 0; z < introItems.length; z++) {
      introItems[z] && tempIntroItems.push(introItems[z]);  // copy non-empty values to the end of the array
    }

    introItems = tempIntroItems;

    //Ok, sort all items with given steps
    introItems.sort(function (a, b) {
      return a.step - b.step;
    });

    //set it to the introJs object
    self._introItems = introItems;

    //add overlay layer to the page
    if(_addOverlayLayer.call(self, targetElm)) {
      //then, start the show
      _nextStep.call(self);

      var skipButton     = targetElm.querySelector('.introjs-skipbutton'),
          nextStepButton = targetElm.querySelector('.introjs-nextbutton');

      self._onKeyDown = function(e) {
        if (e.keyCode === 27 && self._options.exitOnEsc == true) {
          //escape key pressed, exit the intro
          _exitIntro.call(self, targetElm);
          //check if any callback is defined
          if (self._introExitCallback != undefined) {
            self._introExitCallback.call(self);
          }
        } else if(e.keyCode === 37) {
          //left arrow
          _previousStep.call(self);
        } else if (e.keyCode === 39 || e.keyCode === 13) {
          //right arrow or enter
          _nextStep.call(self);
          //prevent default behaviour on hitting Enter, to prevent steps being skipped in some browsers
          if(e.preventDefault) {
            e.preventDefault();
          } else {
            e.returnValue = false;
          }
        }
      };

      self._onResize = function(e) {
        _setHelperLayerPosition.call(self, document.querySelector('.introjs-helperLayer'));
      };

      if (window.addEventListener) {
        if (this._options.keyboardNavigation) {
          window.addEventListener('keydown', self._onKeyDown, true);
        }
        //for window resize
        window.addEventListener("resize", self._onResize, true);
      } else if (document.attachEvent) { //IE
        if (this._options.keyboardNavigation) {
          document.attachEvent('onkeydown', self._onKeyDown);
        }
        //for window resize
        document.attachEvent("onresize", self._onResize);
      }
    }
    return false;
  }

 /*
   * makes a copy of the object
   * @api private
   * @method _cloneObject
  */
  function _cloneObject(object) {
      if (object == null || typeof (object) != 'object' || typeof (object.nodeType) != 'undefined') {
          return object;
      }
      var temp = {};
      for (var key in object) {
          temp[key] = _cloneObject(object[key]);
      }
      return temp;
  }
  /**
   * Go to specific step of introduction
   *
   * @api private
   * @method _goToStep
   */
  function _goToStep(step) {
    //because steps starts with zero
    this._currentStep = step - 2;
    if (typeof (this._introItems) !== 'undefined') {
      _nextStep.call(this);
    }
  }

  /**
   * Go to next step on intro
   *
   * @api private
   * @method _nextStep
   */
  function _nextStep() {
    this._direction = 'forward';

    if (typeof (this._currentStep) === 'undefined') {
      this._currentStep = 0;
    } else {
      ++this._currentStep;
    }

    if ((this._introItems.length) <= this._currentStep) {
      //end of the intro
      //check if any callback is defined
      if (typeof (this._introCompleteCallback) === 'function') {
        this._introCompleteCallback.call(this);
      }
      _exitIntro.call(this, this._targetElement);
      return;
    }

    var nextStep = this._introItems[this._currentStep];
    if (typeof (this._introBeforeChangeCallback) !== 'undefined') {
      this._introBeforeChangeCallback.call(this, nextStep.element);
    }

    _showElement.call(this, nextStep);
  }

  /**
   * Go to previous step on intro
   *
   * @api private
   * @method _nextStep
   */
  function _previousStep() {
    this._direction = 'backward';

    if (this._currentStep === 0) {
      return false;
    }

    var nextStep = this._introItems[--this._currentStep];
    if (typeof (this._introBeforeChangeCallback) !== 'undefined') {
      this._introBeforeChangeCallback.call(this, nextStep.element);
    }

    _showElement.call(this, nextStep);
  }

  /**
   * Exit from intro
   *
   * @api private
   * @method _exitIntro
   * @param {Object} targetElement
   */
  function _exitIntro(targetElement) {
    //remove overlay layer from the page
    var overlayLayer = targetElement.querySelector('.introjs-overlay');

    //return if intro already completed or skipped
    if (overlayLayer == null) {
      return;
    }

    //for fade-out animation
    overlayLayer.style.opacity = 0;
    setTimeout(function () {
      if (overlayLayer.parentNode) {
        overlayLayer.parentNode.removeChild(overlayLayer);
      }
    }, 500);

    //remove all helper layers
    var helperLayer = targetElement.querySelector('.introjs-helperLayer');
    if (helperLayer) {
      helperLayer.parentNode.removeChild(helperLayer);
    }

    //remove intro floating element
    var floatingElement = document.querySelector('.introjsFloatingElement');
    if (floatingElement) {
      floatingElement.parentNode.removeChild(floatingElement);
    }

    //remove `introjs-showElement` class from the element
    var showElement = document.querySelector('.introjs-showElement');
    if (showElement) {
      showElement.className = showElement.className.replace(/introjs-[a-zA-Z]+/g, '').replace(/^\s+|\s+$/g, ''); // This is a manual trim.
    }

    //remove `introjs-fixParent` class from the elements
    var fixParents = document.querySelectorAll('.introjs-fixParent');
    if (fixParents && fixParents.length > 0) {
      for (var i = fixParents.length - 1; i >= 0; i--) {
        fixParents[i].className = fixParents[i].className.replace(/introjs-fixParent/g, '').replace(/^\s+|\s+$/g, '');
      };
    }

    //clean listeners
    if (window.removeEventListener) {
      window.removeEventListener('keydown', this._onKeyDown, true);
    } else if (document.detachEvent) { //IE
      document.detachEvent('onkeydown', this._onKeyDown);
    }

    //set the step to zero
    this._currentStep = undefined;
  }

  /**
   * Render tooltip box in the page
   *
   * @api private
   * @method _placeTooltip
   * @param {Object} targetElement
   * @param {Object} tooltipLayer
   * @param {Object} arrowLayer
   */
  function _placeTooltip(targetElement, tooltipLayer, arrowLayer, helperNumberLayer) {
    var tooltipCssClass = '',
        currentStepObj,
        tooltipOffset,
        targetElementOffset;

    //reset the old style
    tooltipLayer.style.top        = null;
    tooltipLayer.style.right      = null;
    tooltipLayer.style.bottom     = null;
    tooltipLayer.style.left       = null;
    tooltipLayer.style.marginLeft = null;
    tooltipLayer.style.marginTop  = null;

    arrowLayer.style.display = 'inherit';

    if (typeof(helperNumberLayer) != 'undefined' && helperNumberLayer != null) {
      helperNumberLayer.style.top  = null;
      helperNumberLayer.style.left = null;
    }

    //prevent error when `this._currentStep` is undefined
    if (!this._introItems[this._currentStep]) return;

    //if we have a custom css class for each step
    currentStepObj = this._introItems[this._currentStep];
    if (typeof (currentStepObj.tooltipClass) === 'string') {
      tooltipCssClass = currentStepObj.tooltipClass;
    } else {
      tooltipCssClass = this._options.tooltipClass;
    }

    tooltipLayer.className = ('introjs-tooltip ' + tooltipCssClass).replace(/^\s+|\s+$/g, '');

    //custom css class for tooltip boxes
    var tooltipCssClass = this._options.tooltipClass;

    currentTooltipPosition = this._introItems[this._currentStep].position;
    switch (currentTooltipPosition) {
      case 'top':
        tooltipLayer.style.left = '15px';
        tooltipLayer.style.top = '-' + (_getOffset(tooltipLayer).height + 10) + 'px';
        arrowLayer.className = 'introjs-arrow bottom';
        break;
      case 'right':
        tooltipLayer.style.left = (_getOffset(targetElement).width + 20) + 'px';
        arrowLayer.className = 'introjs-arrow left';
        break;
      case 'left':
        if (this._options.showStepNumbers == true) {
          tooltipLayer.style.top = '15px';
        }
        tooltipLayer.style.right = (_getOffset(targetElement).width + 20) + 'px';
        arrowLayer.className = 'introjs-arrow right';
        break;
      case 'floating':
        arrowLayer.style.display = 'none';

        //we have to adjust the top and left of layer manually for intro items without element
        tooltipOffset = _getOffset(tooltipLayer);

        tooltipLayer.style.left   = '50%';
        tooltipLayer.style.top    = '50%';
        tooltipLayer.style.marginLeft = '-' + (tooltipOffset.width / 2)  + 'px';
        tooltipLayer.style.marginTop  = '-' + (tooltipOffset.height / 2) + 'px';

        if (typeof(helperNumberLayer) != 'undefined' && helperNumberLayer != null) {
          helperNumberLayer.style.left = '-' + ((tooltipOffset.width / 2) + 18) + 'px';
          helperNumberLayer.style.top  = '-' + ((tooltipOffset.height / 2) + 18) + 'px';
        }

        break;
      case 'bottom-right-aligned':
        arrowLayer.className      = 'introjs-arrow top-right';
        tooltipLayer.style.right  = '0px';
        tooltipLayer.style.bottom = '-' + (_getOffset(tooltipLayer).height + 10) + 'px';
        break;
      case 'bottom-middle-aligned':
        targetElementOffset = _getOffset(targetElement);
        tooltipOffset       = _getOffset(tooltipLayer);

        arrowLayer.className      = 'introjs-arrow top-middle';
        tooltipLayer.style.left   = (targetElementOffset.width / 2 - tooltipOffset.width / 2) + 'px';
        tooltipLayer.style.bottom = '-' + (tooltipOffset.height + 10) + 'px';
        break;
      case 'bottom-left-aligned':
      // Bottom-left-aligned is the same as the default bottom
      case 'bottom':
      // Bottom going to follow the default behavior
      default:
        tooltipLayer.style.bottom = '-' + (_getOffset(tooltipLayer).height + 10) + 'px';
        arrowLayer.className = 'introjs-arrow top';
        break;
    }
  }

  /**
   * Update the position of the helper layer on the screen
   *
   * @api private
   * @method _setHelperLayerPosition
   * @param {Object} helperLayer
   */
  function _setHelperLayerPosition(helperLayer) {
    if (helperLayer) {
      //prevent error when `this._currentStep` in undefined
      if (!this._introItems[this._currentStep]) return;

      var currentElement  = this._introItems[this._currentStep],
          elementPosition = _getOffset(currentElement.element),
          widthHeightPadding = 10;

      if (currentElement.position == 'floating') {
        widthHeightPadding = 0;
      }

      //set new position to helper layer
      helperLayer.setAttribute('style', 'width: ' + (elementPosition.width  + widthHeightPadding)  + 'px; ' +
                                        'height:' + (elementPosition.height + widthHeightPadding)  + 'px; ' +
                                        'top:'    + (elementPosition.top    - 5)   + 'px;' +
                                        'left: '  + (elementPosition.left   - 5)   + 'px;');
    }
  }

  /**
   * Show an element on the page
   *
   * @api private
   * @method _showElement
   * @param {Object} targetElement
   */
  function _showElement(targetElement) {

    if (typeof (this._introChangeCallback) !== 'undefined') {
        this._introChangeCallback.call(this, targetElement.element);
    }

    var self = this,
        oldHelperLayer = document.querySelector('.introjs-helperLayer'),
        elementPosition = _getOffset(targetElement.element);

    if (oldHelperLayer != null) {
      var oldHelperNumberLayer = oldHelperLayer.querySelector('.introjs-helperNumberLayer'),
          oldtooltipLayer      = oldHelperLayer.querySelector('.introjs-tooltiptext'),
          oldArrowLayer        = oldHelperLayer.querySelector('.introjs-arrow'),
          oldtooltipContainer  = oldHelperLayer.querySelector('.introjs-tooltip'),
          skipTooltipButton    = oldHelperLayer.querySelector('.introjs-skipbutton'),
          prevTooltipButton    = oldHelperLayer.querySelector('.introjs-prevbutton'),
          nextTooltipButton    = oldHelperLayer.querySelector('.introjs-nextbutton');

      //hide the tooltip
      oldtooltipContainer.style.opacity = 0;

      if (oldHelperNumberLayer != null) {
        var lastIntroItem = this._introItems[(targetElement.step - 2 >= 0 ? targetElement.step - 2 : 0)];

        if (lastIntroItem != null && (this._direction == 'forward' && lastIntroItem.position == 'floating') || (this._direction == 'backward' && targetElement.position == 'floating')) {
          oldHelperNumberLayer.style.opacity = 0;
        }
      }

      //set new position to helper layer
      _setHelperLayerPosition.call(self, oldHelperLayer);

      //remove `introjs-fixParent` class from the elements
      var fixParents = document.querySelectorAll('.introjs-fixParent');
      if (fixParents && fixParents.length > 0) {
        for (var i = fixParents.length - 1; i >= 0; i--) {
          fixParents[i].className = fixParents[i].className.replace(/introjs-fixParent/g, '').replace(/^\s+|\s+$/g, '');
        };
      }

      //remove old classes
      var oldShowElement = document.querySelector('.introjs-showElement');
      oldShowElement.className = oldShowElement.className.replace(/introjs-[a-zA-Z]+/g, '').replace(/^\s+|\s+$/g, '');
      //we should wait until the CSS3 transition is competed (it's 0.3 sec) to prevent incorrect `height` and `width` calculation
      if (self._lastShowElementTimer) {
        clearTimeout(self._lastShowElementTimer);
      }
      self._lastShowElementTimer = setTimeout(function() {
        //set current step to the label
        if (oldHelperNumberLayer != null) {
          oldHelperNumberLayer.innerHTML = targetElement.step;
        }
        //set current tooltip text
        oldtooltipLayer.innerHTML = targetElement.intro;
        //set the tooltip position
        _placeTooltip.call(self, targetElement.element, oldtooltipContainer, oldArrowLayer, oldHelperNumberLayer);

        //change active bullet
        oldHelperLayer.querySelector('.introjs-bullets li > a.active').className = '';
        oldHelperLayer.querySelector('.introjs-bullets li > a[data-stepnumber="' + targetElement.step + '"]').className = 'active';

        //show the tooltip
        oldtooltipContainer.style.opacity = 1;
        if (oldHelperNumberLayer) oldHelperNumberLayer.style.opacity = 1;
      }, 350);

    } else {
      var helperLayer       = document.createElement('div'),
          arrowLayer        = document.createElement('div'),
          tooltipLayer      = document.createElement('div'),
          tooltipTextLayer  = document.createElement('div'),
          bulletsLayer      = document.createElement('div'),
          buttonsLayer      = document.createElement('div');

      helperLayer.className = 'introjs-helperLayer';

      //set new position to helper layer
      _setHelperLayerPosition.call(self, helperLayer);

      //add helper layer to target element
      this._targetElement.appendChild(helperLayer);

      arrowLayer.className = 'introjs-arrow';

      tooltipTextLayer.className = 'introjs-tooltiptext';
      tooltipTextLayer.innerHTML = targetElement.intro;

      bulletsLayer.className = 'introjs-bullets';

      if (this._options.showBullets === false) {
        bulletsLayer.style.display = 'none';
      }

      var ulContainer = document.createElement('ul');

      for (var i = 0, stepsLength = this._introItems.length; i < stepsLength; i++) {
        var innerLi    = document.createElement('li');
        var anchorLink = document.createElement('a');

        anchorLink.onclick = function() {
          self.goToStep(this.getAttribute('data-stepnumber'));
        };

        if (i === 0) anchorLink.className = "active";

        anchorLink.href = 'javascript:void(0);';
        anchorLink.innerHTML = "&nbsp;";
        anchorLink.setAttribute('data-stepnumber', this._introItems[i].step);

        innerLi.appendChild(anchorLink);
        ulContainer.appendChild(innerLi);
      }

      bulletsLayer.appendChild(ulContainer);

      buttonsLayer.className = 'introjs-tooltipbuttons';
      if (this._options.showButtons === false) {
        buttonsLayer.style.display = 'none';
      }

      tooltipLayer.className = 'introjs-tooltip';
      tooltipLayer.appendChild(tooltipTextLayer);
      tooltipLayer.appendChild(bulletsLayer);

      //add helper layer number
      if (this._options.showStepNumbers == true) {
        var helperNumberLayer = document.createElement('span');
        helperNumberLayer.className = 'introjs-helperNumberLayer';
        helperNumberLayer.innerHTML = targetElement.step;
        helperLayer.appendChild(helperNumberLayer);
      }
      tooltipLayer.appendChild(arrowLayer);
      helperLayer.appendChild(tooltipLayer);

      //next button
      var nextTooltipButton = document.createElement('a');

      nextTooltipButton.onclick = function() {
        if (self._introItems.length - 1 != self._currentStep) {
          _nextStep.call(self);
        }
      };

      nextTooltipButton.href = 'javascript:void(0);';
      nextTooltipButton.innerHTML = this._options.nextLabel;

      //previous button
      var prevTooltipButton = document.createElement('a');

      prevTooltipButton.onclick = function() {
        if (self._currentStep != 0) {
          _previousStep.call(self);
        }
      };

      prevTooltipButton.href = 'javascript:void(0);';
      prevTooltipButton.innerHTML = this._options.prevLabel;

      //skip button
      var skipTooltipButton = document.createElement('a');
      skipTooltipButton.className = 'introjs-button introjs-skipbutton';
      skipTooltipButton.href = 'javascript:void(0);';
      skipTooltipButton.innerHTML = this._options.skipLabel;

      skipTooltipButton.onclick = function() {
        if (self._introItems.length - 1 == self._currentStep && typeof (self._introCompleteCallback) === 'function') {
          self._introCompleteCallback.call(self);
        }

        if (self._introItems.length - 1 != self._currentStep && typeof (self._introExitCallback) === 'function') {
          self._introExitCallback.call(self);
        }

        _exitIntro.call(self, self._targetElement);
      };

      buttonsLayer.appendChild(skipTooltipButton);

      //in order to prevent displaying next/previous button always
      if (this._introItems.length > 1) {
        buttonsLayer.appendChild(prevTooltipButton);
        buttonsLayer.appendChild(nextTooltipButton);
      }

      tooltipLayer.appendChild(buttonsLayer);

      //set proper position
      _placeTooltip.call(self, targetElement.element, tooltipLayer, arrowLayer, helperNumberLayer);
    }

    if (this._currentStep == 0 && this._introItems.length > 1) {
      prevTooltipButton.className = 'introjs-button introjs-prevbutton introjs-disabled';
      nextTooltipButton.className = 'introjs-button introjs-nextbutton';
      skipTooltipButton.innerHTML = this._options.skipLabel;
    } else if (this._introItems.length - 1 == this._currentStep || this._introItems.length == 1) {
      skipTooltipButton.innerHTML = this._options.doneLabel;
      prevTooltipButton.className = 'introjs-button introjs-prevbutton';
      nextTooltipButton.className = 'introjs-button introjs-nextbutton introjs-disabled';
    } else {
      prevTooltipButton.className = 'introjs-button introjs-prevbutton';
      nextTooltipButton.className = 'introjs-button introjs-nextbutton';
      skipTooltipButton.innerHTML = this._options.skipLabel;
    }

    //Set focus on "next" button, so that hitting Enter always moves you onto the next step
    nextTooltipButton.focus();

    //add target element position style
    targetElement.element.className += ' introjs-showElement';

    var currentElementPosition = _getPropValue(targetElement.element, 'position');
    if (currentElementPosition !== 'absolute' &&
        currentElementPosition !== 'relative') {
      //change to new intro item
      targetElement.element.className += ' introjs-relativePosition';
    }

    var parentElm = targetElement.element.parentNode;
    while (parentElm != null) {
      if (parentElm.tagName.toLowerCase() === 'body') break;

      //fix The Stacking Contenxt problem.
      //More detail: https://developer.mozilla.org/en-US/docs/Web/Guide/CSS/Understanding_z_index/The_stacking_context
      var zIndex = _getPropValue(parentElm, 'z-index');
      var opacity = parseFloat(_getPropValue(parentElm, 'opacity'));
      if (/[0-9]+/.test(zIndex) || opacity < 1) {
        parentElm.className += ' introjs-fixParent';
      }

      parentElm = parentElm.parentNode;
    }

    if (!_elementInViewport(targetElement.element) && this._options.scrollToElement === true) {
      var rect = targetElement.element.getBoundingClientRect(),
        winHeight=_getWinSize().height,
        top = rect.bottom - (rect.bottom - rect.top),
        bottom = rect.bottom - winHeight;

      //Scroll up
      if (top < 0 || targetElement.element.clientHeight > winHeight) {
        window.scrollBy(0, top - 30); // 30px padding from edge to look nice

      //Scroll down
      } else {
        window.scrollBy(0, bottom + 100); // 70px + 30px padding from edge to look nice
      }
    }

    if (typeof (this._introAfterChangeCallback) !== 'undefined') {
        this._introAfterChangeCallback.call(this, targetElement.element);
    }
  }

  /**
   * Get an element CSS property on the page
   * Thanks to JavaScript Kit: http://www.javascriptkit.com/dhtmltutors/dhtmlcascade4.shtml
   *
   * @api private
   * @method _getPropValue
   * @param {Object} element
   * @param {String} propName
   * @returns Element's property value
   */
  function _getPropValue (element, propName) {
    var propValue = '';
    if (element.currentStyle) { //IE
      propValue = element.currentStyle[propName];
    } else if (document.defaultView && document.defaultView.getComputedStyle) { //Others
      propValue = document.defaultView.getComputedStyle(element, null).getPropertyValue(propName);
    }

    //Prevent exception in IE
    if (propValue && propValue.toLowerCase) {
      return propValue.toLowerCase();
    } else {
      return propValue;
    }
  }

  /**
   * Provides a cross-browser way to get the screen dimensions
   * via: http://stackoverflow.com/questions/5864467/internet-explorer-innerheight
   *
   * @api private
   * @method _getWinSize
   * @returns {Object} width and height attributes
   */
  function _getWinSize() {
    if (window.innerWidth != undefined) {
      return { width: window.innerWidth, height: window.innerHeight };
    } else {
      var D = document.documentElement;
      return { width: D.clientWidth, height: D.clientHeight };
    }
  }

  /**
   * Add overlay layer to the page
   * http://stackoverflow.com/questions/123999/how-to-tell-if-a-dom-element-is-visible-in-the-current-viewport
   *
   * @api private
   * @method _elementInViewport
   * @param {Object} el
   */
  function _elementInViewport(el) {
    var rect = el.getBoundingClientRect();

    return (
      rect.top >= 0 &&
      rect.left >= 0 &&
      (rect.bottom+80) <= window.innerHeight && // add 80 to get the text right
      rect.right <= window.innerWidth
    );
  }

  /**
   * Add overlay layer to the page
   *
   * @api private
   * @method _addOverlayLayer
   * @param {Object} targetElm
   */
  function _addOverlayLayer(targetElm) {
    var overlayLayer = document.createElement('div'),
        styleText = '',
        self = this;

    //set css class name
    overlayLayer.className = 'introjs-overlay';

    //check if the target element is body, we should calculate the size of overlay layer in a better way
    if (targetElm.tagName.toLowerCase() === 'body') {
      styleText += 'top: 0;bottom: 0; left: 0;right: 0;position: fixed;';
      overlayLayer.setAttribute('style', styleText);
    } else {
      //set overlay layer position
      var elementPosition = _getOffset(targetElm);
      if (elementPosition) {
        styleText += 'width: ' + elementPosition.width + 'px; height:' + elementPosition.height + 'px; top:' + elementPosition.top + 'px;left: ' + elementPosition.left + 'px;';
        overlayLayer.setAttribute('style', styleText);
      }
    }

    targetElm.appendChild(overlayLayer);

    overlayLayer.onclick = function() {
      if (self._options.exitOnOverlayClick == true) {
        _exitIntro.call(self, targetElm);

        //check if any callback is defined
        if (self._introExitCallback != undefined) {
          self._introExitCallback.call(self);
        }
      }
    };

    setTimeout(function() {
      styleText += 'opacity: ' + self._options.overlayOpacity.toString() + ';';
      overlayLayer.setAttribute('style', styleText);
    }, 10);

    return true;
  }

  /**
   * Get an element position on the page
   * Thanks to `meouw`: http://stackoverflow.com/a/442474/375966
   *
   * @api private
   * @method _getOffset
   * @param {Object} element
   * @returns Element's position info
   */
  function _getOffset(element) {
    var elementPosition = {};

    //set width
    elementPosition.width = element.offsetWidth;

    //set height
    elementPosition.height = element.offsetHeight;

    //calculate element top and left
    var _x = 0;
    var _y = 0;
    while (element && !isNaN(element.offsetLeft) && !isNaN(element.offsetTop)) {
      _x += element.offsetLeft;
      _y += element.offsetTop;
      element = element.offsetParent;
    }
    //set top
    elementPosition.top = _y;
    //set left
    elementPosition.left = _x;

    return elementPosition;
  }

  /**
   * Overwrites obj1's values with obj2's and adds obj2's if non existent in obj1
   * via: http://stackoverflow.com/questions/171251/how-can-i-merge-properties-of-two-javascript-objects-dynamically
   *
   * @param obj1
   * @param obj2
   * @returns obj3 a new object based on obj1 and obj2
   */
  function _mergeOptions(obj1,obj2) {
    var obj3 = {};
    for (var attrname in obj1) { obj3[attrname] = obj1[attrname]; }
    for (var attrname in obj2) { obj3[attrname] = obj2[attrname]; }
    return obj3;
  }

  var introJs = function (targetElm) {
    if (typeof (targetElm) === 'object') {
      //Ok, create a new instance
      return new IntroJs(targetElm);

    } else if (typeof (targetElm) === 'string') {
      //select the target element with query selector
      var targetElement = document.querySelector(targetElm);

      if (targetElement) {
        return new IntroJs(targetElement);
      } else {
        throw new Error('There is no element with given selector.');
      }
    } else {
      return new IntroJs(document.body);
    }
  };

  /**
   * Current IntroJs version
   *
   * @property version
   * @type String
   */
  introJs.version = VERSION;

  //Prototype
  introJs.fn = IntroJs.prototype = {
    clone: function () {
      return new IntroJs(this);
    },
    setOption: function(option, value) {
      this._options[option] = value;
      return this;
    },
    setOptions: function(options) {
      this._options = _mergeOptions(this._options, options);
      return this;
    },
    start: function () {
      _introForElement.call(this, this._targetElement);
      return this;
    },
    goToStep: function(step) {
      _goToStep.call(this, step);
      return this;
    },
    nextStep: function() {
      _nextStep.call(this);
      return this;
    },
    previousStep: function() {
      _previousStep.call(this);
      return this;
    },
    exit: function() {
      _exitIntro.call(this, this._targetElement);
    },
    refresh: function() {
      _setHelperLayerPosition.call(this, document.querySelector('.introjs-helperLayer'));
      return this;
    },
    onbeforechange: function(providedCallback) {
      if (typeof (providedCallback) === 'function') {
        this._introBeforeChangeCallback = providedCallback;
      } else {
        throw new Error('Provided callback for onbeforechange was not a function');
      }
      return this;
    },
    onchange: function(providedCallback) {
      if (typeof (providedCallback) === 'function') {
        this._introChangeCallback = providedCallback;
      } else {
        throw new Error('Provided callback for onchange was not a function.');
      }
      return this;
    },
    onafterchange: function(providedCallback) {
      if (typeof (providedCallback) === 'function') {
        this._introAfterChangeCallback = providedCallback;
      } else {
        throw new Error('Provided callback for onafterchange was not a function');
      }
      return this;
    },
    oncomplete: function(providedCallback) {
      if (typeof (providedCallback) === 'function') {
        this._introCompleteCallback = providedCallback;
      } else {
        throw new Error('Provided callback for oncomplete was not a function.');
      }
      return this;
    },
    onexit: function(providedCallback) {
      if (typeof (providedCallback) === 'function') {
        this._introExitCallback = providedCallback;
      } else {
        throw new Error('Provided callback for onexit was not a function.');
      }
      return this;
    }
  };

  exports.introJs = introJs;
  return introJs;
}));


/***/ }),

/***/ "./node_modules/jquery.cookie/jquery.cookie.js":
/***/ (function(module, exports, __webpack_require__) {

var __WEBPACK_AMD_DEFINE_FACTORY__, __WEBPACK_AMD_DEFINE_ARRAY__, __WEBPACK_AMD_DEFINE_RESULT__;/*** IMPORTS FROM imports-loader ***/
var $ = __webpack_require__("./node_modules/jquery/dist/jquery.js");
var jQuery = __webpack_require__("./node_modules/jquery/dist/jquery.js");
(function() {

/*!
 * jQuery Cookie Plugin v1.4.1
 * https://github.com/carhartl/jquery-cookie
 *
 * Copyright 2013 Klaus Hartl
 * Released under the MIT license
 */
(function (factory) {
	if (true) {
		// AMD
		!(__WEBPACK_AMD_DEFINE_ARRAY__ = [__webpack_require__("./node_modules/jquery/dist/jquery.js")], __WEBPACK_AMD_DEFINE_FACTORY__ = (factory),
				__WEBPACK_AMD_DEFINE_RESULT__ = (typeof __WEBPACK_AMD_DEFINE_FACTORY__ === 'function' ?
				(__WEBPACK_AMD_DEFINE_FACTORY__.apply(exports, __WEBPACK_AMD_DEFINE_ARRAY__)) : __WEBPACK_AMD_DEFINE_FACTORY__),
				__WEBPACK_AMD_DEFINE_RESULT__ !== undefined && (module.exports = __WEBPACK_AMD_DEFINE_RESULT__));
	} else if (typeof exports === 'object') {
		// CommonJS
		factory(require('jquery'));
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function ($) {

	var pluses = /\+/g;

	function encode(s) {
		return config.raw ? s : encodeURIComponent(s);
	}

	function decode(s) {
		return config.raw ? s : decodeURIComponent(s);
	}

	function stringifyCookieValue(value) {
		return encode(config.json ? JSON.stringify(value) : String(value));
	}

	function parseCookieValue(s) {
		if (s.indexOf('"') === 0) {
			// This is a quoted cookie as according to RFC2068, unescape...
			s = s.slice(1, -1).replace(/\\"/g, '"').replace(/\\\\/g, '\\');
		}

		try {
			// Replace server-side written pluses with spaces.
			// If we can't decode the cookie, ignore it, it's unusable.
			// If we can't parse the cookie, ignore it, it's unusable.
			s = decodeURIComponent(s.replace(pluses, ' '));
			return config.json ? JSON.parse(s) : s;
		} catch(e) {}
	}

	function read(s, converter) {
		var value = config.raw ? s : parseCookieValue(s);
		return $.isFunction(converter) ? converter(value) : value;
	}

	var config = $.cookie = function (key, value, options) {

		// Write

		if (value !== undefined && !$.isFunction(value)) {
			options = $.extend({}, config.defaults, options);

			if (typeof options.expires === 'number') {
				var days = options.expires, t = options.expires = new Date();
				t.setTime(+t + days * 864e+5);
			}

			return (document.cookie = [
				encode(key), '=', stringifyCookieValue(value),
				options.expires ? '; expires=' + options.expires.toUTCString() : '', // use expires attribute, max-age is not supported by IE
				options.path    ? '; path=' + options.path : '',
				options.domain  ? '; domain=' + options.domain : '',
				options.secure  ? '; secure' : ''
			].join(''));
		}

		// Read

		var result = key ? undefined : {};

		// To prevent the for loop in the first place assign an empty array
		// in case there are no cookies at all. Also prevents odd result when
		// calling $.cookie().
		var cookies = document.cookie ? document.cookie.split('; ') : [];

		for (var i = 0, l = cookies.length; i < l; i++) {
			var parts = cookies[i].split('=');
			var name = decode(parts.shift());
			var cookie = parts.join('=');

			if (key && key === name) {
				// If second argument (value) is a function it's a converter...
				result = read(cookie, value);
				break;
			}

			// Prevent storing a cookie that we couldn't decode.
			if (!key && (cookie = read(cookie)) !== undefined) {
				result[name] = cookie;
			}
		}

		return result;
	};

	config.defaults = {};

	$.removeCookie = function (key, options) {
		if ($.cookie(key) === undefined) {
			return false;
		}

		// Must not alter options, thus extending a fresh object...
		$.cookie(key, '', $.extend({}, options, { expires: -1 }));
		return !$.cookie(key);
	};

}));

}.call(window));

/***/ }),

/***/ "./src/main/webapp/media/scripts/webpack/config/introjs.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
Object.defineProperty(__webpack_exports__, "__esModule", { value: true });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_jquery__ = __webpack_require__("./node_modules/jquery/dist/jquery.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_jquery___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_jquery__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_intro_js__ = __webpack_require__("./node_modules/intro.js/intro.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_intro_js___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_intro_js__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_underscore__ = __webpack_require__("./node_modules/underscore/underscore.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_underscore___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_underscore__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_jquery_cookie__ = __webpack_require__("./node_modules/jquery.cookie/jquery.cookie.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_jquery_cookie___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_3_jquery_cookie__);





/* harmony default export */ __webpack_exports__["default"] = (function (introName) {
	// eslint-disable-next-line new-cap
	var intro = new __WEBPACK_IMPORTED_MODULE_1_intro_js___default.a.introJs();
	intro.name = introName;

	intro.setOptions({
		/* Next button label in tooltip box */
		nextLabel: 'Next &rarr;',
		/* Previous button label in tooltip box */
		prevLabel: '&larr; Back',
		/* Skip button label in tooltip box */
		skipLabel: 'Read Later',
		/* Done button label in tooltip box */
		doneLabel: 'Finish',
		/* Default tooltip box position */
		tooltipPosition: 'bottom',
		/* Next CSS class for tooltip boxes */
		tooltipClass: 'wm-introjs',
		/* Close introduction when pressing Escape button? */
		exitOnEsc: true,
		/* Close introduction when clicking on overlay layer? */
		exitOnOverlayClick: true,
		/* Show step numbers in introduction? */
		showStepNumbers: false,
		/* Let user use keyboard to navigate the tour? */
		keyboardNavigation: true,
		/* Show tour control buttons? */
		showButtons: true,
		/* Show tour bullets? */
		showBullets: true,
		/* Scroll to highlighted element? */
		scrollToElement: true,
		/* Set the overlay opacity */
		overlayOpacity: 0.8
	});

	// When user completes a joyride, we set a cookie and post the visit to the
	// visited_resources table
	var _oncomplete = intro.oncomplete;
	intro.oncomplete = __WEBPACK_IMPORTED_MODULE_2_underscore___default.a.compose(_oncomplete, function (func) {
		return function () {
			// Record visit to this resource
			__WEBPACK_IMPORTED_MODULE_0_jquery___default.a.ajax({
				url: '/tracking/merge?resourceName=' + intro.name
			});

			// Set cookie to avoid unnecessary ajax request
			__WEBPACK_IMPORTED_MODULE_0_jquery___default.a.cookie(intro.name, 'viewed', { expires: 365, path: '/' });

			// Google analytics
			ga('send', 'pageview', '/intro/' + intro.name + '/finish');

			func();
		};
	});
	intro.oncomplete(__WEBPACK_IMPORTED_MODULE_2_underscore___default.a.noop);

	// Send virtual pageview for each step of the joyride that is hit
	var _onafterchage = intro.onafterchange;
	intro.onafterchange = __WEBPACK_IMPORTED_MODULE_2_underscore___default.a.compose(_onafterchage, function (func) {
		return function (e) {
			// Google analytics
			ga('send', 'pageview', '/intro/' + intro.name + '/' + intro._currentStep);
			func(e);
		};
	});
	intro.onafterchange(__WEBPACK_IMPORTED_MODULE_2_underscore___default.a.noop);

	// Watch once ensures the joyride does not run if the user has already seen it
	intro.watchOnce = function () {
		// Check for cookie first... that's fast and cheap
		if (__WEBPACK_IMPORTED_MODULE_0_jquery___default.a.cookie(intro.name)) {
			// User has already viewed this joyride
			return;
		}

		// Request list of viewed joyrides from server next...
		__WEBPACK_IMPORTED_MODULE_0_jquery___default.a.ajax({
			url: '/tracking',
			success: function success(response) {
				// Find intro.name in the visited resources list?
				if (__WEBPACK_IMPORTED_MODULE_2_underscore___default.a.contains(response.visitedList, intro.name)) {
					// User has already viewed this
					return;
				}

				// User does not appear to have viewed this... show it!
				intro.start();
			}
		});
	};

	return intro;
});

/***/ })

});
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vLi9+L2ludHJvLmpzL2ludHJvLmpzIiwid2VicGFjazovLy8uL34vanF1ZXJ5LmNvb2tpZS9qcXVlcnkuY29va2llLmpzIiwid2VicGFjazovLy8uL3NyYy9tYWluL3dlYmFwcC9tZWRpYS9zY3JpcHRzL3dlYnBhY2svY29uZmlnL2ludHJvanMuanMiXSwibmFtZXMiOlsiaW50cm9OYW1lIiwiaW50cm8iLCJJbnRyb0pzIiwiaW50cm9KcyIsIm5hbWUiLCJzZXRPcHRpb25zIiwibmV4dExhYmVsIiwicHJldkxhYmVsIiwic2tpcExhYmVsIiwiZG9uZUxhYmVsIiwidG9vbHRpcFBvc2l0aW9uIiwidG9vbHRpcENsYXNzIiwiZXhpdE9uRXNjIiwiZXhpdE9uT3ZlcmxheUNsaWNrIiwic2hvd1N0ZXBOdW1iZXJzIiwia2V5Ym9hcmROYXZpZ2F0aW9uIiwic2hvd0J1dHRvbnMiLCJzaG93QnVsbGV0cyIsInNjcm9sbFRvRWxlbWVudCIsIm92ZXJsYXlPcGFjaXR5IiwiX29uY29tcGxldGUiLCJvbmNvbXBsZXRlIiwiXyIsImNvbXBvc2UiLCJmdW5jIiwiJCIsImFqYXgiLCJ1cmwiLCJjb29raWUiLCJleHBpcmVzIiwicGF0aCIsImdhIiwibm9vcCIsIl9vbmFmdGVyY2hhZ2UiLCJvbmFmdGVyY2hhbmdlIiwiZSIsIl9jdXJyZW50U3RlcCIsIndhdGNoT25jZSIsInN1Y2Nlc3MiLCJyZXNwb25zZSIsImNvbnRhaW5zIiwidmlzaXRlZExpc3QiLCJzdGFydCJdLCJtYXBwaW5ncyI6Ijs7Ozs7QUFBQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLEdBQUc7QUFDSDtBQUNBO0FBQ0EsR0FBRztBQUNIO0FBQ0E7QUFDQTtBQUNBLENBQUM7QUFDRDtBQUNBOztBQUVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQSw2QkFBNkI7QUFDN0I7QUFDQSx3QkFBd0I7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Q2pFO0FBQ0E7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7O0FBRUEsS0FBSztBQUNMO0FBQ0E7O0FBRUE7QUFDQTtBQUNBLE9BQU87QUFDUDtBQUNBO0FBQ0EsS0FBSztBQUNMO0FBQ0E7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLEtBQUs7QUFDTDtBQUNBO0FBQ0E7QUFDQSxLQUFLO0FBQ0w7QUFDQTtBQUNBO0FBQ0EsS0FBSztBQUNMO0FBQ0E7QUFDQTtBQUNBLEtBQUs7QUFDTDtBQUNBO0FBQ0E7QUFDQSxLQUFLO0FBQ0w7QUFDQTtBQUNBO0FBQ0EsS0FBSztBQUNMO0FBQ0E7QUFDQTtBQUNBLEtBQUs7QUFDTDtBQUNBO0FBQ0EsS0FBSztBQUNMO0FBQ0E7QUFDQTtBQUNBLEtBQUs7QUFDTDtBQUNBO0FBQ0E7QUFDQSxPQUFPO0FBQ1A7QUFDQTtBQUNBO0FBQ0EsS0FBSztBQUNMO0FBQ0E7QUFDQTtBQUNBLE9BQU87QUFDUDtBQUNBO0FBQ0E7QUFDQSxLQUFLO0FBQ0w7QUFDQTtBQUNBO0FBQ0EsT0FBTztBQUNQO0FBQ0E7QUFDQTtBQUNBLEtBQUs7QUFDTDtBQUNBO0FBQ0E7QUFDQSxPQUFPO0FBQ1A7QUFDQTtBQUNBO0FBQ0EsS0FBSztBQUNMO0FBQ0E7QUFDQTtBQUNBLE9BQU87QUFDUDtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQSxDQUFDOzs7Ozs7OztBQ3pnQ0Q7QUFDQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUFBO0FBQUE7QUFBQTtBQUNBLEVBQUU7QUFDRjtBQUNBO0FBQ0EsRUFBRTtBQUNGO0FBQ0E7QUFDQTtBQUNBLENBQUM7O0FBRUQ7O0FBRUE7QUFDQTtBQUNBOztBQUVBO0FBQ0E7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7QUFDQTtBQUNB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QyxJQUFGLENBQU87QUFDTkMsUUFBSyxXQURDO0FBRU5XLFVBRk0sbUJBRUdDLFFBRkgsRUFFYTtBQUNsQjtBQUNBLFFBQUksa0RBQUFqQixDQUFFa0IsUUFBRixDQUFXRCxTQUFTRSxXQUFwQixFQUFpQ3hDLE1BQU1HLElBQXZDLENBQUosRUFBa0Q7QUFDakQ7QUFDQTtBQUNBOztBQUVEO0FBQ0FILFVBQU15QyxLQUFOO0FBQ0E7QUFYSyxHQUFQO0FBYUEsRUFyQkQ7O0FBdUJBLFFBQU96QyxLQUFQO0FBQ0EsQ0E3RkQsRSIsImZpbGUiOiJJbnRyb0pzLmpzIiwic291cmNlc0NvbnRlbnQiOlsiLyoqXG4gKiBJbnRyby5qcyB2MC45LjBcbiAqIGh0dHBzOi8vZ2l0aHViLmNvbS91c2FibGljYS9pbnRyby5qc1xuICogTUlUIGxpY2Vuc2VkXG4gKlxuICogQ29weXJpZ2h0IChDKSAyMDEzIHVzYWJsaS5jYSAtIEEgd2Vla2VuZCBwcm9qZWN0IGJ5IEFmc2hpbiBNZWhyYWJhbmkgKEBhZnNoaW5tZWgpXG4gKi9cblxuKGZ1bmN0aW9uIChyb290LCBmYWN0b3J5KSB7XG4gIGlmICh0eXBlb2YgZXhwb3J0cyA9PT0gJ29iamVjdCcpIHtcbiAgICAvLyBDb21tb25KU1xuICAgIGZhY3RvcnkoZXhwb3J0cyk7XG4gIH0gZWxzZSBpZiAodHlwZW9mIGRlZmluZSA9PT0gJ2Z1bmN0aW9uJyAmJiBkZWZpbmUuYW1kKSB7XG4gICAgLy8gQU1ELiBSZWdpc3RlciBhcyBhbiBhbm9ueW1vdXMgbW9kdWxlLlxuICAgIGRlZmluZShbJ2V4cG9ydHMnXSwgZmFjdG9yeSk7XG4gIH0gZWxzZSB7XG4gICAgLy8gQnJvd3NlciBnbG9iYWxzXG4gICAgZmFjdG9yeShyb290KTtcbiAgfVxufSAodGhpcywgZnVuY3Rpb24gKGV4cG9ydHMpIHtcbiAgLy9EZWZhdWx0IGNvbmZpZy92YXJpYWJsZXNcbiAgdmFyIFZFUlNJT04gPSAnMC45LjAnO1xuXG4gIC8qKlxuICAgKiBJbnRyb0pzIG1haW4gY2xhc3NcbiAgICpcbiAgICogQGNsYXNzIEludHJvSnNcbiAgICovXG4gIGZ1bmN0aW9uIEludHJvSnMob2JqKSB7XG4gICAgdGhpcy5fdGFyZ2V0RWxlbWVudCA9IG9iajtcblxuICAgIHRoaXMuX29wdGlvbnMgPSB7XG4gICAgICAvKiBOZXh0IGJ1dHRvbiBsYWJlbCBpbiB0b29sdGlwIGJveCAqL1xuICAgICAgbmV4dExhYmVsOiAnTmV4dCAmcmFycjsnLFxuICAgICAgLyogUHJldmlvdXMgYnV0dG9uIGxhYmVsIGluIHRvb2x0aXAgYm94ICovXG4gICAgICBwcmV2TGFiZWw6ICcmbGFycjsgQmFjaycsXG4gICAgICAvKiBTa2lwIGJ1dHRvbiBsYWJlbCBpbiB0b29sdGlwIGJveCAqL1xuICAgICAgc2tpcExhYmVsOiAnU2tpcCcsXG4gICAgICAvKiBEb25lIGJ1dHRvbiBsYWJlbCBpbiB0b29sdGlwIGJveCAqL1xuICAgICAgZG9uZUxhYmVsOiAnRG9uZScsXG4gICAgICAvKiBEZWZhdWx0IHRvb2x0aXAgYm94IHBvc2l0aW9uICovXG4gICAgICB0b29sdGlwUG9zaXRpb246ICdib3R0b20nLFxuICAgICAgLyogTmV4dCBDU1MgY2xhc3MgZm9yIHRvb2x0aXAgYm94ZXMgKi9cbiAgICAgIHRvb2x0aXBDbGFzczogJycsXG4gICAgICAvKiBDbG9zZSBpbnRyb2R1Y3Rpb24gd2hlbiBwcmVzc2luZyBFc2NhcGUgYnV0dG9uPyAqL1xuICAgICAgZXhpdE9uRXNjOiB0cnVlLFxuICAgICAgLyogQ2xvc2UgaW50cm9kdWN0aW9uIHdoZW4gY2xpY2tpbmcgb24gb3ZlcmxheSBsYXllcj8gKi9cbiAgICAgIGV4aXRPbk92ZXJsYXlDbGljazogdHJ1ZSxcbiAgICAgIC8qIFNob3cgc3RlcCBudW1iZXJzIGluIGludHJvZHVjdGlvbj8gKi9cbiAgICAgIHNob3dTdGVwTnVtYmVyczogdHJ1ZSxcbiAgICAgIC8qIExldCB1c2VyIHVzZSBrZXlib2FyZCB0byBuYXZpZ2F0ZSB0aGUgdG91cj8gKi9cbiAgICAgIGtleWJvYXJkTmF2aWdhdGlvbjogdHJ1ZSxcbiAgICAgIC8qIFNob3cgdG91ciBjb250cm9sIGJ1dHRvbnM/ICovXG4gICAgICBzaG93QnV0dG9uczogdHJ1ZSxcbiAgICAgIC8qIFNob3cgdG91ciBidWxsZXRzPyAqL1xuICAgICAgc2hvd0J1bGxldHM6IHRydWUsXG4gICAgICAvKiBTY3JvbGwgdG8gaGlnaGxpZ2h0ZWQgZWxlbWVudD8gKi9cbiAgICAgIHNjcm9sbFRvRWxlbWVudDogdHJ1ZSxcbiAgICAgIC8qIFNldCB0aGUgb3ZlcmxheSBvcGFjaXR5ICovXG4gICAgICBvdmVybGF5T3BhY2l0eTogMC44XG4gICAgfTtcbiAgfVxuXG4gIC8qKlxuICAgKiBJbml0aWF0ZSBhIG5ldyBpbnRyb2R1Y3Rpb24vZ3VpZGUgZnJvbSBhbiBlbGVtZW50IGluIHRoZSBwYWdlXG4gICAqXG4gICAqIEBhcGkgcHJpdmF0ZVxuICAgKiBAbWV0aG9kIF9pbnRyb0ZvckVsZW1lbnRcbiAgICogQHBhcmFtIHtPYmplY3R9IHRhcmdldEVsbVxuICAgKiBAcmV0dXJucyB7Qm9vbGVhbn0gU3VjY2VzcyBvciBub3Q/XG4gICAqL1xuICBmdW5jdGlvbiBfaW50cm9Gb3JFbGVtZW50KHRhcmdldEVsbSkge1xuICAgIHZhciBpbnRyb0l0ZW1zID0gW10sXG4gICAgICAgIHNlbGYgPSB0aGlzO1xuXG4gICAgaWYgKHRoaXMuX29wdGlvbnMuc3RlcHMpIHtcbiAgICAgIC8vdXNlIHN0ZXBzIHBhc3NlZCBwcm9ncmFtbWF0aWNhbGx5XG4gICAgICB2YXIgYWxsSW50cm9TdGVwcyA9IFtdO1xuXG4gICAgICBmb3IgKHZhciBpID0gMCwgc3RlcHNMZW5ndGggPSB0aGlzLl9vcHRpb25zLnN0ZXBzLmxlbmd0aDsgaSA8IHN0ZXBzTGVuZ3RoOyBpKyspIHtcbiAgICAgICAgdmFyIGN1cnJlbnRJdGVtID0gX2Nsb25lT2JqZWN0KHRoaXMuX29wdGlvbnMuc3RlcHNbaV0pO1xuICAgICAgICAvL3NldCB0aGUgc3RlcFxuICAgICAgICBjdXJyZW50SXRlbS5zdGVwID0gaW50cm9JdGVtcy5sZW5ndGggKyAxO1xuICAgICAgICAvL3VzZSBxdWVyeVNlbGVjdG9yIGZ1bmN0aW9uIG9ubHkgd2hlbiBkZXZlbG9wZXIgdXNlZCBDU1Mgc2VsZWN0b3JcbiAgICAgICAgaWYgKHR5cGVvZihjdXJyZW50SXRlbS5lbGVtZW50KSA9PT0gJ3N0cmluZycpIHtcbiAgICAgICAgICAvL2dyYWIgdGhlIGVsZW1lbnQgd2l0aCBnaXZlbiBzZWxlY3RvciBmcm9tIHRoZSBwYWdlXG4gICAgICAgICAgY3VycmVudEl0ZW0uZWxlbWVudCA9IGRvY3VtZW50LnF1ZXJ5U2VsZWN0b3IoY3VycmVudEl0ZW0uZWxlbWVudCk7XG4gICAgICAgIH1cblxuICAgICAgICAvL2ludHJvIHdpdGhvdXQgZWxlbWVudFxuICAgICAgICBpZiAodHlwZW9mKGN1cnJlbnRJdGVtLmVsZW1lbnQpID09PSAndW5kZWZpbmVkJyB8fCBjdXJyZW50SXRlbS5lbGVtZW50ID09IG51bGwpIHtcbiAgICAgICAgICB2YXIgZmxvYXRpbmdFbGVtZW50UXVlcnkgPSBkb2N1bWVudC5xdWVyeVNlbGVjdG9yKFwiLmludHJvanNGbG9hdGluZ0VsZW1lbnRcIik7XG5cbiAgICAgICAgICBpZiAoZmxvYXRpbmdFbGVtZW50UXVlcnkgPT0gbnVsbCkge1xuICAgICAgICAgICAgZmxvYXRpbmdFbGVtZW50UXVlcnkgPSBkb2N1bWVudC5jcmVhdGVFbGVtZW50KCdkaXYnKTtcbiAgICAgICAgICAgIGZsb2F0aW5nRWxlbWVudFF1ZXJ5LmNsYXNzTmFtZSA9ICdpbnRyb2pzRmxvYXRpbmdFbGVtZW50JztcblxuICAgICAgICAgICAgZG9jdW1lbnQuYm9keS5hcHBlbmRDaGlsZChmbG9hdGluZ0VsZW1lbnRRdWVyeSk7XG4gICAgICAgICAgfVxuXG4gICAgICAgICAgY3VycmVudEl0ZW0uZWxlbWVudCAgPSBmbG9hdGluZ0VsZW1lbnRRdWVyeTtcbiAgICAgICAgICBjdXJyZW50SXRlbS5wb3NpdGlvbiA9ICdmbG9hdGluZyc7XG4gICAgICAgIH1cblxuICAgICAgICBpZiAoY3VycmVudEl0ZW0uZWxlbWVudCAhPSBudWxsKSB7XG4gICAgICAgICAgaW50cm9JdGVtcy5wdXNoKGN1cnJlbnRJdGVtKTtcbiAgICAgICAgfVxuICAgICAgfVxuXG4gICAgfSBlbHNlIHtcbiAgICAgICAvL3VzZSBzdGVwcyBmcm9tIGRhdGEtKiBhbm5vdGF0aW9uc1xuICAgICAgdmFyIGFsbEludHJvU3RlcHMgPSB0YXJnZXRFbG0ucXVlcnlTZWxlY3RvckFsbCgnKltkYXRhLWludHJvXScpO1xuICAgICAgLy9pZiB0aGVyZSdzIG5vIGVsZW1lbnQgdG8gaW50cm9cbiAgICAgIGlmIChhbGxJbnRyb1N0ZXBzLmxlbmd0aCA8IDEpIHtcbiAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgfVxuXG4gICAgICAvL2ZpcnN0IGFkZCBpbnRybyBpdGVtcyB3aXRoIGRhdGEtc3RlcFxuICAgICAgZm9yICh2YXIgaSA9IDAsIGVsbXNMZW5ndGggPSBhbGxJbnRyb1N0ZXBzLmxlbmd0aDsgaSA8IGVsbXNMZW5ndGg7IGkrKykge1xuICAgICAgICB2YXIgY3VycmVudEVsZW1lbnQgPSBhbGxJbnRyb1N0ZXBzW2ldO1xuICAgICAgICB2YXIgc3RlcCA9IHBhcnNlSW50KGN1cnJlbnRFbGVtZW50LmdldEF0dHJpYnV0ZSgnZGF0YS1zdGVwJyksIDEwKTtcblxuICAgICAgICBpZiAoc3RlcCA+IDApIHtcbiAgICAgICAgICBpbnRyb0l0ZW1zW3N0ZXAgLSAxXSA9IHtcbiAgICAgICAgICAgIGVsZW1lbnQ6IGN1cnJlbnRFbGVtZW50LFxuICAgICAgICAgICAgaW50cm86IGN1cnJlbnRFbGVtZW50LmdldEF0dHJpYnV0ZSgnZGF0YS1pbnRybycpLFxuICAgICAgICAgICAgc3RlcDogcGFyc2VJbnQoY3VycmVudEVsZW1lbnQuZ2V0QXR0cmlidXRlKCdkYXRhLXN0ZXAnKSwgMTApLFxuICAgICAgICAgICAgdG9vbHRpcENsYXNzOiBjdXJyZW50RWxlbWVudC5nZXRBdHRyaWJ1dGUoJ2RhdGEtdG9vbHRpcENsYXNzJyksXG4gICAgICAgICAgICBwb3NpdGlvbjogY3VycmVudEVsZW1lbnQuZ2V0QXR0cmlidXRlKCdkYXRhLXBvc2l0aW9uJykgfHwgdGhpcy5fb3B0aW9ucy50b29sdGlwUG9zaXRpb25cbiAgICAgICAgICB9O1xuICAgICAgICB9XG4gICAgICB9XG5cbiAgICAgIC8vbmV4dCBhZGQgaW50cm8gaXRlbXMgd2l0aG91dCBkYXRhLXN0ZXBcbiAgICAgIC8vdG9kbzogd2UgbmVlZCBhIGNsZWFudXAgaGVyZSwgdHdvIGxvb3BzIGFyZSByZWR1bmRhbnRcbiAgICAgIHZhciBuZXh0U3RlcCA9IDA7XG4gICAgICBmb3IgKHZhciBpID0gMCwgZWxtc0xlbmd0aCA9IGFsbEludHJvU3RlcHMubGVuZ3RoOyBpIDwgZWxtc0xlbmd0aDsgaSsrKSB7XG4gICAgICAgIHZhciBjdXJyZW50RWxlbWVudCA9IGFsbEludHJvU3RlcHNbaV07XG5cbiAgICAgICAgaWYgKGN1cnJlbnRFbGVtZW50LmdldEF0dHJpYnV0ZSgnZGF0YS1zdGVwJykgPT0gbnVsbCkge1xuXG4gICAgICAgICAgd2hpbGUgKHRydWUpIHtcbiAgICAgICAgICAgIGlmICh0eXBlb2YgaW50cm9JdGVtc1tuZXh0U3RlcF0gPT0gJ3VuZGVmaW5lZCcpIHtcbiAgICAgICAgICAgICAgYnJlYWs7XG4gICAgICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgICBuZXh0U3RlcCsrO1xuICAgICAgICAgICAgfVxuICAgICAgICAgIH1cblxuICAgICAgICAgIGludHJvSXRlbXNbbmV4dFN0ZXBdID0ge1xuICAgICAgICAgICAgZWxlbWVudDogY3VycmVudEVsZW1lbnQsXG4gICAgICAgICAgICBpbnRybzogY3VycmVudEVsZW1lbnQuZ2V0QXR0cmlidXRlKCdkYXRhLWludHJvJyksXG4gICAgICAgICAgICBzdGVwOiBuZXh0U3RlcCArIDEsXG4gICAgICAgICAgICB0b29sdGlwQ2xhc3M6IGN1cnJlbnRFbGVtZW50LmdldEF0dHJpYnV0ZSgnZGF0YS10b29sdGlwQ2xhc3MnKSxcbiAgICAgICAgICAgIHBvc2l0aW9uOiBjdXJyZW50RWxlbWVudC5nZXRBdHRyaWJ1dGUoJ2RhdGEtcG9zaXRpb24nKSB8fCB0aGlzLl9vcHRpb25zLnRvb2x0aXBQb3NpdGlvblxuICAgICAgICAgIH07XG4gICAgICAgIH1cbiAgICAgIH1cbiAgICB9XG5cbiAgICAvL3JlbW92aW5nIHVuZGVmaW5lZC9udWxsIGVsZW1lbnRzXG4gICAgdmFyIHRlbXBJbnRyb0l0ZW1zID0gW107XG4gICAgZm9yICh2YXIgeiA9IDA7IHogPCBpbnRyb0l0ZW1zLmxlbmd0aDsgeisrKSB7XG4gICAgICBpbnRyb0l0ZW1zW3pdICYmIHRlbXBJbnRyb0l0ZW1zLnB1c2goaW50cm9JdGVtc1t6XSk7ICAvLyBjb3B5IG5vbi1lbXB0eSB2YWx1ZXMgdG8gdGhlIGVuZCBvZiB0aGUgYXJyYXlcbiAgICB9XG5cbiAgICBpbnRyb0l0ZW1zID0gdGVtcEludHJvSXRlbXM7XG5cbiAgICAvL09rLCBzb3J0IGFsbCBpdGVtcyB3aXRoIGdpdmVuIHN0ZXBzXG4gICAgaW50cm9JdGVtcy5zb3J0KGZ1bmN0aW9uIChhLCBiKSB7XG4gICAgICByZXR1cm4gYS5zdGVwIC0gYi5zdGVwO1xuICAgIH0pO1xuXG4gICAgLy9zZXQgaXQgdG8gdGhlIGludHJvSnMgb2JqZWN0XG4gICAgc2VsZi5faW50cm9JdGVtcyA9IGludHJvSXRlbXM7XG5cbiAgICAvL2FkZCBvdmVybGF5IGxheWVyIHRvIHRoZSBwYWdlXG4gICAgaWYoX2FkZE92ZXJsYXlMYXllci5jYWxsKHNlbGYsIHRhcmdldEVsbSkpIHtcbiAgICAgIC8vdGhlbiwgc3RhcnQgdGhlIHNob3dcbiAgICAgIF9uZXh0U3RlcC5jYWxsKHNlbGYpO1xuXG4gICAgICB2YXIgc2tpcEJ1dHRvbiAgICAgPSB0YXJnZXRFbG0ucXVlcnlTZWxlY3RvcignLmludHJvanMtc2tpcGJ1dHRvbicpLFxuICAgICAgICAgIG5leHRTdGVwQnV0dG9uID0gdGFyZ2V0RWxtLnF1ZXJ5U2VsZWN0b3IoJy5pbnRyb2pzLW5leHRidXR0b24nKTtcblxuICAgICAgc2VsZi5fb25LZXlEb3duID0gZnVuY3Rpb24oZSkge1xuICAgICAgICBpZiAoZS5rZXlDb2RlID09PSAyNyAmJiBzZWxmLl9vcHRpb25zLmV4aXRPbkVzYyA9PSB0cnVlKSB7XG4gICAgICAgICAgLy9lc2NhcGUga2V5IHByZXNzZWQsIGV4aXQgdGhlIGludHJvXG4gICAgICAgICAgX2V4aXRJbnRyby5jYWxsKHNlbGYsIHRhcmdldEVsbSk7XG4gICAgICAgICAgLy9jaGVjayBpZiBhbnkgY2FsbGJhY2sgaXMgZGVmaW5lZFxuICAgICAgICAgIGlmIChzZWxmLl9pbnRyb0V4aXRDYWxsYmFjayAhPSB1bmRlZmluZWQpIHtcbiAgICAgICAgICAgIHNlbGYuX2ludHJvRXhpdENhbGxiYWNrLmNhbGwoc2VsZik7XG4gICAgICAgICAgfVxuICAgICAgICB9IGVsc2UgaWYoZS5rZXlDb2RlID09PSAzNykge1xuICAgICAgICAgIC8vbGVmdCBhcnJvd1xuICAgICAgICAgIF9wcmV2aW91c1N0ZXAuY2FsbChzZWxmKTtcbiAgICAgICAgfSBlbHNlIGlmIChlLmtleUNvZGUgPT09IDM5IHx8IGUua2V5Q29kZSA9PT0gMTMpIHtcbiAgICAgICAgICAvL3JpZ2h0IGFycm93IG9yIGVudGVyXG4gICAgICAgICAgX25leHRTdGVwLmNhbGwoc2VsZik7XG4gICAgICAgICAgLy9wcmV2ZW50IGRlZmF1bHQgYmVoYXZpb3VyIG9uIGhpdHRpbmcgRW50ZXIsIHRvIHByZXZlbnQgc3RlcHMgYmVpbmcgc2tpcHBlZCBpbiBzb21lIGJyb3dzZXJzXG4gICAgICAgICAgaWYoZS5wcmV2ZW50RGVmYXVsdCkge1xuICAgICAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuICAgICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICBlLnJldHVyblZhbHVlID0gZmFsc2U7XG4gICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICB9O1xuXG4gICAgICBzZWxmLl9vblJlc2l6ZSA9IGZ1bmN0aW9uKGUpIHtcbiAgICAgICAgX3NldEhlbHBlckxheWVyUG9zaXRpb24uY2FsbChzZWxmLCBkb2N1bWVudC5xdWVyeVNlbGVjdG9yKCcuaW50cm9qcy1oZWxwZXJMYXllcicpKTtcbiAgICAgIH07XG5cbiAgICAgIGlmICh3aW5kb3cuYWRkRXZlbnRMaXN0ZW5lcikge1xuICAgICAgICBpZiAodGhpcy5fb3B0aW9ucy5rZXlib2FyZE5hdmlnYXRpb24pIHtcbiAgICAgICAgICB3aW5kb3cuYWRkRXZlbnRMaXN0ZW5lcigna2V5ZG93bicsIHNlbGYuX29uS2V5RG93biwgdHJ1ZSk7XG4gICAgICAgIH1cbiAgICAgICAgLy9mb3Igd2luZG93IHJlc2l6ZVxuICAgICAgICB3aW5kb3cuYWRkRXZlbnRMaXN0ZW5lcihcInJlc2l6ZVwiLCBzZWxmLl9vblJlc2l6ZSwgdHJ1ZSk7XG4gICAgICB9IGVsc2UgaWYgKGRvY3VtZW50LmF0dGFjaEV2ZW50KSB7IC8vSUVcbiAgICAgICAgaWYgKHRoaXMuX29wdGlvbnMua2V5Ym9hcmROYXZpZ2F0aW9uKSB7XG4gICAgICAgICAgZG9jdW1lbnQuYXR0YWNoRXZlbnQoJ29ua2V5ZG93bicsIHNlbGYuX29uS2V5RG93bik7XG4gICAgICAgIH1cbiAgICAgICAgLy9mb3Igd2luZG93IHJlc2l6ZVxuICAgICAgICBkb2N1bWVudC5hdHRhY2hFdmVudChcIm9ucmVzaXplXCIsIHNlbGYuX29uUmVzaXplKTtcbiAgICAgIH1cbiAgICB9XG4gICAgcmV0dXJuIGZhbHNlO1xuICB9XG5cbiAvKlxuICAgKiBtYWtlcyBhIGNvcHkgb2YgdGhlIG9iamVjdFxuICAgKiBAYXBpIHByaXZhdGVcbiAgICogQG1ldGhvZCBfY2xvbmVPYmplY3RcbiAgKi9cbiAgZnVuY3Rpb24gX2Nsb25lT2JqZWN0KG9iamVjdCkge1xuICAgICAgaWYgKG9iamVjdCA9PSBudWxsIHx8IHR5cGVvZiAob2JqZWN0KSAhPSAnb2JqZWN0JyB8fCB0eXBlb2YgKG9iamVjdC5ub2RlVHlwZSkgIT0gJ3VuZGVmaW5lZCcpIHtcbiAgICAgICAgICByZXR1cm4gb2JqZWN0O1xuICAgICAgfVxuICAgICAgdmFyIHRlbXAgPSB7fTtcbiAgICAgIGZvciAodmFyIGtleSBpbiBvYmplY3QpIHtcbiAgICAgICAgICB0ZW1wW2tleV0gPSBfY2xvbmVPYmplY3Qob2JqZWN0W2tleV0pO1xuICAgICAgfVxuICAgICAgcmV0dXJuIHRlbXA7XG4gIH1cbiAgLyoqXG4gICAqIEdvIHRvIHNwZWNpZmljIHN0ZXAgb2YgaW50cm9kdWN0aW9uXG4gICAqXG4gICAqIEBhcGkgcHJpdmF0ZVxuICAgKiBAbWV0aG9kIF9nb1RvU3RlcFxuICAgKi9cbiAgZnVuY3Rpb24gX2dvVG9TdGVwKHN0ZXApIHtcbiAgICAvL2JlY2F1c2Ugc3RlcHMgc3RhcnRzIHdpdGggemVyb1xuICAgIHRoaXMuX2N1cnJlbnRTdGVwID0gc3RlcCAtIDI7XG4gICAgaWYgKHR5cGVvZiAodGhpcy5faW50cm9JdGVtcykgIT09ICd1bmRlZmluZWQnKSB7XG4gICAgICBfbmV4dFN0ZXAuY2FsbCh0aGlzKTtcbiAgICB9XG4gIH1cblxuICAvKipcbiAgICogR28gdG8gbmV4dCBzdGVwIG9uIGludHJvXG4gICAqXG4gICAqIEBhcGkgcHJpdmF0ZVxuICAgKiBAbWV0aG9kIF9uZXh0U3RlcFxuICAgKi9cbiAgZnVuY3Rpb24gX25leHRTdGVwKCkge1xuICAgIHRoaXMuX2RpcmVjdGlvbiA9ICdmb3J3YXJkJztcblxuICAgIGlmICh0eXBlb2YgKHRoaXMuX2N1cnJlbnRTdGVwKSA9PT0gJ3VuZGVmaW5lZCcpIHtcbiAgICAgIHRoaXMuX2N1cnJlbnRTdGVwID0gMDtcbiAgICB9IGVsc2Uge1xuICAgICAgKyt0aGlzLl9jdXJyZW50U3RlcDtcbiAgICB9XG5cbiAgICBpZiAoKHRoaXMuX2ludHJvSXRlbXMubGVuZ3RoKSA8PSB0aGlzLl9jdXJyZW50U3RlcCkge1xuICAgICAgLy9lbmQgb2YgdGhlIGludHJvXG4gICAgICAvL2NoZWNrIGlmIGFueSBjYWxsYmFjayBpcyBkZWZpbmVkXG4gICAgICBpZiAodHlwZW9mICh0aGlzLl9pbnRyb0NvbXBsZXRlQ2FsbGJhY2spID09PSAnZnVuY3Rpb24nKSB7XG4gICAgICAgIHRoaXMuX2ludHJvQ29tcGxldGVDYWxsYmFjay5jYWxsKHRoaXMpO1xuICAgICAgfVxuICAgICAgX2V4aXRJbnRyby5jYWxsKHRoaXMsIHRoaXMuX3RhcmdldEVsZW1lbnQpO1xuICAgICAgcmV0dXJuO1xuICAgIH1cblxuICAgIHZhciBuZXh0U3RlcCA9IHRoaXMuX2ludHJvSXRlbXNbdGhpcy5fY3VycmVudFN0ZXBdO1xuICAgIGlmICh0eXBlb2YgKHRoaXMuX2ludHJvQmVmb3JlQ2hhbmdlQ2FsbGJhY2spICE9PSAndW5kZWZpbmVkJykge1xuICAgICAgdGhpcy5faW50cm9CZWZvcmVDaGFuZ2VDYWxsYmFjay5jYWxsKHRoaXMsIG5leHRTdGVwLmVsZW1lbnQpO1xuICAgIH1cblxuICAgIF9zaG93RWxlbWVudC5jYWxsKHRoaXMsIG5leHRTdGVwKTtcbiAgfVxuXG4gIC8qKlxuICAgKiBHbyB0byBwcmV2aW91cyBzdGVwIG9uIGludHJvXG4gICAqXG4gICAqIEBhcGkgcHJpdmF0ZVxuICAgKiBAbWV0aG9kIF9uZXh0U3RlcFxuICAgKi9cbiAgZnVuY3Rpb24gX3ByZXZpb3VzU3RlcCgpIHtcbiAgICB0aGlzLl9kaXJlY3Rpb24gPSAnYmFja3dhcmQnO1xuXG4gICAgaWYgKHRoaXMuX2N1cnJlbnRTdGVwID09PSAwKSB7XG4gICAgICByZXR1cm4gZmFsc2U7XG4gICAgfVxuXG4gICAgdmFyIG5leHRTdGVwID0gdGhpcy5faW50cm9JdGVtc1stLXRoaXMuX2N1cnJlbnRTdGVwXTtcbiAgICBpZiAodHlwZW9mICh0aGlzLl9pbnRyb0JlZm9yZUNoYW5nZUNhbGxiYWNrKSAhPT0gJ3VuZGVmaW5lZCcpIHtcbiAgICAgIHRoaXMuX2ludHJvQmVmb3JlQ2hhbmdlQ2FsbGJhY2suY2FsbCh0aGlzLCBuZXh0U3RlcC5lbGVtZW50KTtcbiAgICB9XG5cbiAgICBfc2hvd0VsZW1lbnQuY2FsbCh0aGlzLCBuZXh0U3RlcCk7XG4gIH1cblxuICAvKipcbiAgICogRXhpdCBmcm9tIGludHJvXG4gICAqXG4gICAqIEBhcGkgcHJpdmF0ZVxuICAgKiBAbWV0aG9kIF9leGl0SW50cm9cbiAgICogQHBhcmFtIHtPYmplY3R9IHRhcmdldEVsZW1lbnRcbiAgICovXG4gIGZ1bmN0aW9uIF9leGl0SW50cm8odGFyZ2V0RWxlbWVudCkge1xuICAgIC8vcmVtb3ZlIG92ZXJsYXkgbGF5ZXIgZnJvbSB0aGUgcGFnZVxuICAgIHZhciBvdmVybGF5TGF5ZXIgPSB0YXJnZXRFbGVtZW50LnF1ZXJ5U2VsZWN0b3IoJy5pbnRyb2pzLW92ZXJsYXknKTtcblxuICAgIC8vcmV0dXJuIGlmIGludHJvIGFscmVhZHkgY29tcGxldGVkIG9yIHNraXBwZWRcbiAgICBpZiAob3ZlcmxheUxheWVyID09IG51bGwpIHtcbiAgICAgIHJldHVybjtcbiAgICB9XG5cbiAgICAvL2ZvciBmYWRlLW91dCBhbmltYXRpb25cbiAgICBvdmVybGF5TGF5ZXIuc3R5bGUub3BhY2l0eSA9IDA7XG4gICAgc2V0VGltZW91dChmdW5jdGlvbiAoKSB7XG4gICAgICBpZiAob3ZlcmxheUxheWVyLnBhcmVudE5vZGUpIHtcbiAgICAgICAgb3ZlcmxheUxheWVyLnBhcmVudE5vZGUucmVtb3ZlQ2hpbGQob3ZlcmxheUxheWVyKTtcbiAgICAgIH1cbiAgICB9LCA1MDApO1xuXG4gICAgLy9yZW1vdmUgYWxsIGhlbHBlciBsYXllcnNcbiAgICB2YXIgaGVscGVyTGF5ZXIgPSB0YXJnZXRFbGVtZW50LnF1ZXJ5U2VsZWN0b3IoJy5pbnRyb2pzLWhlbHBlckxheWVyJyk7XG4gICAgaWYgKGhlbHBlckxheWVyKSB7XG4gICAgICBoZWxwZXJMYXllci5wYXJlbnROb2RlLnJlbW92ZUNoaWxkKGhlbHBlckxheWVyKTtcbiAgICB9XG5cbiAgICAvL3JlbW92ZSBpbnRybyBmbG9hdGluZyBlbGVtZW50XG4gICAgdmFyIGZsb2F0aW5nRWxlbWVudCA9IGRvY3VtZW50LnF1ZXJ5U2VsZWN0b3IoJy5pbnRyb2pzRmxvYXRpbmdFbGVtZW50Jyk7XG4gICAgaWYgKGZsb2F0aW5nRWxlbWVudCkge1xuICAgICAgZmxvYXRpbmdFbGVtZW50LnBhcmVudE5vZGUucmVtb3ZlQ2hpbGQoZmxvYXRpbmdFbGVtZW50KTtcbiAgICB9XG5cbiAgICAvL3JlbW92ZSBgaW50cm9qcy1zaG93RWxlbWVudGAgY2xhc3MgZnJvbSB0aGUgZWxlbWVudFxuICAgIHZhciBzaG93RWxlbWVudCA9IGRvY3VtZW50LnF1ZXJ5U2VsZWN0b3IoJy5pbnRyb2pzLXNob3dFbGVtZW50Jyk7XG4gICAgaWYgKHNob3dFbGVtZW50KSB7XG4gICAgICBzaG93RWxlbWVudC5jbGFzc05hbWUgPSBzaG93RWxlbWVudC5jbGFzc05hbWUucmVwbGFjZSgvaW50cm9qcy1bYS16QS1aXSsvZywgJycpLnJlcGxhY2UoL15cXHMrfFxccyskL2csICcnKTsgLy8gVGhpcyBpcyBhIG1hbnVhbCB0cmltLlxuICAgIH1cblxuICAgIC8vcmVtb3ZlIGBpbnRyb2pzLWZpeFBhcmVudGAgY2xhc3MgZnJvbSB0aGUgZWxlbWVudHNcbiAgICB2YXIgZml4UGFyZW50cyA9IGRvY3VtZW50LnF1ZXJ5U2VsZWN0b3JBbGwoJy5pbnRyb2pzLWZpeFBhcmVudCcpO1xuICAgIGlmIChmaXhQYXJlbnRzICYmIGZpeFBhcmVudHMubGVuZ3RoID4gMCkge1xuICAgICAgZm9yICh2YXIgaSA9IGZpeFBhcmVudHMubGVuZ3RoIC0gMTsgaSA+PSAwOyBpLS0pIHtcbiAgICAgICAgZml4UGFyZW50c1tpXS5jbGFzc05hbWUgPSBmaXhQYXJlbnRzW2ldLmNsYXNzTmFtZS5yZXBsYWNlKC9pbnRyb2pzLWZpeFBhcmVudC9nLCAnJykucmVwbGFjZSgvXlxccyt8XFxzKyQvZywgJycpO1xuICAgICAgfTtcbiAgICB9XG5cbiAgICAvL2NsZWFuIGxpc3RlbmVyc1xuICAgIGlmICh3aW5kb3cucmVtb3ZlRXZlbnRMaXN0ZW5lcikge1xuICAgICAgd2luZG93LnJlbW92ZUV2ZW50TGlzdGVuZXIoJ2tleWRvd24nLCB0aGlzLl9vbktleURvd24sIHRydWUpO1xuICAgIH0gZWxzZSBpZiAoZG9jdW1lbnQuZGV0YWNoRXZlbnQpIHsgLy9JRVxuICAgICAgZG9jdW1lbnQuZGV0YWNoRXZlbnQoJ29ua2V5ZG93bicsIHRoaXMuX29uS2V5RG93bik7XG4gICAgfVxuXG4gICAgLy9zZXQgdGhlIHN0ZXAgdG8gemVyb1xuICAgIHRoaXMuX2N1cnJlbnRTdGVwID0gdW5kZWZpbmVkO1xuICB9XG5cbiAgLyoqXG4gICAqIFJlbmRlciB0b29sdGlwIGJveCBpbiB0aGUgcGFnZVxuICAgKlxuICAgKiBAYXBpIHByaXZhdGVcbiAgICogQG1ldGhvZCBfcGxhY2VUb29sdGlwXG4gICAqIEBwYXJhbSB7T2JqZWN0fSB0YXJnZXRFbGVtZW50XG4gICAqIEBwYXJhbSB7T2JqZWN0fSB0b29sdGlwTGF5ZXJcbiAgICogQHBhcmFtIHtPYmplY3R9IGFycm93TGF5ZXJcbiAgICovXG4gIGZ1bmN0aW9uIF9wbGFjZVRvb2x0aXAodGFyZ2V0RWxlbWVudCwgdG9vbHRpcExheWVyLCBhcnJvd0xheWVyLCBoZWxwZXJOdW1iZXJMYXllcikge1xuICAgIHZhciB0b29sdGlwQ3NzQ2xhc3MgPSAnJyxcbiAgICAgICAgY3VycmVudFN0ZXBPYmosXG4gICAgICAgIHRvb2x0aXBPZmZzZXQsXG4gICAgICAgIHRhcmdldEVsZW1lbnRPZmZzZXQ7XG5cbiAgICAvL3Jlc2V0IHRoZSBvbGQgc3R5bGVcbiAgICB0b29sdGlwTGF5ZXIuc3R5bGUudG9wICAgICAgICA9IG51bGw7XG4gICAgdG9vbHRpcExheWVyLnN0eWxlLnJpZ2h0ICAgICAgPSBudWxsO1xuICAgIHRvb2x0aXBMYXllci5zdHlsZS5ib3R0b20gICAgID0gbnVsbDtcbiAgICB0b29sdGlwTGF5ZXIuc3R5bGUubGVmdCAgICAgICA9IG51bGw7XG4gICAgdG9vbHRpcExheWVyLnN0eWxlLm1hcmdpbkxlZnQgPSBudWxsO1xuICAgIHRvb2x0aXBMYXllci5zdHlsZS5tYXJnaW5Ub3AgID0gbnVsbDtcblxuICAgIGFycm93TGF5ZXIuc3R5bGUuZGlzcGxheSA9ICdpbmhlcml0JztcblxuICAgIGlmICh0eXBlb2YoaGVscGVyTnVtYmVyTGF5ZXIpICE9ICd1bmRlZmluZWQnICYmIGhlbHBlck51bWJlckxheWVyICE9IG51bGwpIHtcbiAgICAgIGhlbHBlck51bWJlckxheWVyLnN0eWxlLnRvcCAgPSBudWxsO1xuICAgICAgaGVscGVyTnVtYmVyTGF5ZXIuc3R5bGUubGVmdCA9IG51bGw7XG4gICAgfVxuXG4gICAgLy9wcmV2ZW50IGVycm9yIHdoZW4gYHRoaXMuX2N1cnJlbnRTdGVwYCBpcyB1bmRlZmluZWRcbiAgICBpZiAoIXRoaXMuX2ludHJvSXRlbXNbdGhpcy5fY3VycmVudFN0ZXBdKSByZXR1cm47XG5cbiAgICAvL2lmIHdlIGhhdmUgYSBjdXN0b20gY3NzIGNsYXNzIGZvciBlYWNoIHN0ZXBcbiAgICBjdXJyZW50U3RlcE9iaiA9IHRoaXMuX2ludHJvSXRlbXNbdGhpcy5fY3VycmVudFN0ZXBdO1xuICAgIGlmICh0eXBlb2YgKGN1cnJlbnRTdGVwT2JqLnRvb2x0aXBDbGFzcykgPT09ICdzdHJpbmcnKSB7XG4gICAgICB0b29sdGlwQ3NzQ2xhc3MgPSBjdXJyZW50U3RlcE9iai50b29sdGlwQ2xhc3M7XG4gICAgfSBlbHNlIHtcbiAgICAgIHRvb2x0aXBDc3NDbGFzcyA9IHRoaXMuX29wdGlvbnMudG9vbHRpcENsYXNzO1xuICAgIH1cblxuICAgIHRvb2x0aXBMYXllci5jbGFzc05hbWUgPSAoJ2ludHJvanMtdG9vbHRpcCAnICsgdG9vbHRpcENzc0NsYXNzKS5yZXBsYWNlKC9eXFxzK3xcXHMrJC9nLCAnJyk7XG5cbiAgICAvL2N1c3RvbSBjc3MgY2xhc3MgZm9yIHRvb2x0aXAgYm94ZXNcbiAgICB2YXIgdG9vbHRpcENzc0NsYXNzID0gdGhpcy5fb3B0aW9ucy50b29sdGlwQ2xhc3M7XG5cbiAgICBjdXJyZW50VG9vbHRpcFBvc2l0aW9uID0gdGhpcy5faW50cm9JdGVtc1t0aGlzLl9jdXJyZW50U3RlcF0ucG9zaXRpb247XG4gICAgc3dpdGNoIChjdXJyZW50VG9vbHRpcFBvc2l0aW9uKSB7XG4gICAgICBjYXNlICd0b3AnOlxuICAgICAgICB0b29sdGlwTGF5ZXIuc3R5bGUubGVmdCA9ICcxNXB4JztcbiAgICAgICAgdG9vbHRpcExheWVyLnN0eWxlLnRvcCA9ICctJyArIChfZ2V0T2Zmc2V0KHRvb2x0aXBMYXllcikuaGVpZ2h0ICsgMTApICsgJ3B4JztcbiAgICAgICAgYXJyb3dMYXllci5jbGFzc05hbWUgPSAnaW50cm9qcy1hcnJvdyBib3R0b20nO1xuICAgICAgICBicmVhaztcbiAgICAgIGNhc2UgJ3JpZ2h0JzpcbiAgICAgICAgdG9vbHRpcExheWVyLnN0eWxlLmxlZnQgPSAoX2dldE9mZnNldCh0YXJnZXRFbGVtZW50KS53aWR0aCArIDIwKSArICdweCc7XG4gICAgICAgIGFycm93TGF5ZXIuY2xhc3NOYW1lID0gJ2ludHJvanMtYXJyb3cgbGVmdCc7XG4gICAgICAgIGJyZWFrO1xuICAgICAgY2FzZSAnbGVmdCc6XG4gICAgICAgIGlmICh0aGlzLl9vcHRpb25zLnNob3dTdGVwTnVtYmVycyA9PSB0cnVlKSB7XG4gICAgICAgICAgdG9vbHRpcExheWVyLnN0eWxlLnRvcCA9ICcxNXB4JztcbiAgICAgICAgfVxuICAgICAgICB0b29sdGlwTGF5ZXIuc3R5bGUucmlnaHQgPSAoX2dldE9mZnNldCh0YXJnZXRFbGVtZW50KS53aWR0aCArIDIwKSArICdweCc7XG4gICAgICAgIGFycm93TGF5ZXIuY2xhc3NOYW1lID0gJ2ludHJvanMtYXJyb3cgcmlnaHQnO1xuICAgICAgICBicmVhaztcbiAgICAgIGNhc2UgJ2Zsb2F0aW5nJzpcbiAgICAgICAgYXJyb3dMYXllci5zdHlsZS5kaXNwbGF5ID0gJ25vbmUnO1xuXG4gICAgICAgIC8vd2UgaGF2ZSB0byBhZGp1c3QgdGhlIHRvcCBhbmQgbGVmdCBvZiBsYXllciBtYW51YWxseSBmb3IgaW50cm8gaXRlbXMgd2l0aG91dCBlbGVtZW50XG4gICAgICAgIHRvb2x0aXBPZmZzZXQgPSBfZ2V0T2Zmc2V0KHRvb2x0aXBMYXllcik7XG5cbiAgICAgICAgdG9vbHRpcExheWVyLnN0eWxlLmxlZnQgICA9ICc1MCUnO1xuICAgICAgICB0b29sdGlwTGF5ZXIuc3R5bGUudG9wICAgID0gJzUwJSc7XG4gICAgICAgIHRvb2x0aXBMYXllci5zdHlsZS5tYXJnaW5MZWZ0ID0gJy0nICsgKHRvb2x0aXBPZmZzZXQud2lkdGggLyAyKSAgKyAncHgnO1xuICAgICAgICB0b29sdGlwTGF5ZXIuc3R5bGUubWFyZ2luVG9wICA9ICctJyArICh0b29sdGlwT2Zmc2V0LmhlaWdodCAvIDIpICsgJ3B4JztcblxuICAgICAgICBpZiAodHlwZW9mKGhlbHBlck51bWJlckxheWVyKSAhPSAndW5kZWZpbmVkJyAmJiBoZWxwZXJOdW1iZXJMYXllciAhPSBudWxsKSB7XG4gICAgICAgICAgaGVscGVyTnVtYmVyTGF5ZXIuc3R5bGUubGVmdCA9ICctJyArICgodG9vbHRpcE9mZnNldC53aWR0aCAvIDIpICsgMTgpICsgJ3B4JztcbiAgICAgICAgICBoZWxwZXJOdW1iZXJMYXllci5zdHlsZS50b3AgID0gJy0nICsgKCh0b29sdGlwT2Zmc2V0LmhlaWdodCAvIDIpICsgMTgpICsgJ3B4JztcbiAgICAgICAgfVxuXG4gICAgICAgIGJyZWFrO1xuICAgICAgY2FzZSAnYm90dG9tLXJpZ2h0LWFsaWduZWQnOlxuICAgICAgICBhcnJvd0xheWVyLmNsYXNzTmFtZSAgICAgID0gJ2ludHJvanMtYXJyb3cgdG9wLXJpZ2h0JztcbiAgICAgICAgdG9vbHRpcExheWVyLnN0eWxlLnJpZ2h0ICA9ICcwcHgnO1xuICAgICAgICB0b29sdGlwTGF5ZXIuc3R5bGUuYm90dG9tID0gJy0nICsgKF9nZXRPZmZzZXQodG9vbHRpcExheWVyKS5oZWlnaHQgKyAxMCkgKyAncHgnO1xuICAgICAgICBicmVhaztcbiAgICAgIGNhc2UgJ2JvdHRvbS1taWRkbGUtYWxpZ25lZCc6XG4gICAgICAgIHRhcmdldEVsZW1lbnRPZmZzZXQgPSBfZ2V0T2Zmc2V0KHRhcmdldEVsZW1lbnQpO1xuICAgICAgICB0b29sdGlwT2Zmc2V0ICAgICAgID0gX2dldE9mZnNldCh0b29sdGlwTGF5ZXIpO1xuXG4gICAgICAgIGFycm93TGF5ZXIuY2xhc3NOYW1lICAgICAgPSAnaW50cm9qcy1hcnJvdyB0b3AtbWlkZGxlJztcbiAgICAgICAgdG9vbHRpcExheWVyLnN0eWxlLmxlZnQgICA9ICh0YXJnZXRFbGVtZW50T2Zmc2V0LndpZHRoIC8gMiAtIHRvb2x0aXBPZmZzZXQud2lkdGggLyAyKSArICdweCc7XG4gICAgICAgIHRvb2x0aXBMYXllci5zdHlsZS5ib3R0b20gPSAnLScgKyAodG9vbHRpcE9mZnNldC5oZWlnaHQgKyAxMCkgKyAncHgnO1xuICAgICAgICBicmVhaztcbiAgICAgIGNhc2UgJ2JvdHRvbS1sZWZ0LWFsaWduZWQnOlxuICAgICAgLy8gQm90dG9tLWxlZnQtYWxpZ25lZCBpcyB0aGUgc2FtZSBhcyB0aGUgZGVmYXVsdCBib3R0b21cbiAgICAgIGNhc2UgJ2JvdHRvbSc6XG4gICAgICAvLyBCb3R0b20gZ29pbmcgdG8gZm9sbG93IHRoZSBkZWZhdWx0IGJlaGF2aW9yXG4gICAgICBkZWZhdWx0OlxuICAgICAgICB0b29sdGlwTGF5ZXIuc3R5bGUuYm90dG9tID0gJy0nICsgKF9nZXRPZmZzZXQodG9vbHRpcExheWVyKS5oZWlnaHQgKyAxMCkgKyAncHgnO1xuICAgICAgICBhcnJvd0xheWVyLmNsYXNzTmFtZSA9ICdpbnRyb2pzLWFycm93IHRvcCc7XG4gICAgICAgIGJyZWFrO1xuICAgIH1cbiAgfVxuXG4gIC8qKlxuICAgKiBVcGRhdGUgdGhlIHBvc2l0aW9uIG9mIHRoZSBoZWxwZXIgbGF5ZXIgb24gdGhlIHNjcmVlblxuICAgKlxuICAgKiBAYXBpIHByaXZhdGVcbiAgICogQG1ldGhvZCBfc2V0SGVscGVyTGF5ZXJQb3NpdGlvblxuICAgKiBAcGFyYW0ge09iamVjdH0gaGVscGVyTGF5ZXJcbiAgICovXG4gIGZ1bmN0aW9uIF9zZXRIZWxwZXJMYXllclBvc2l0aW9uKGhlbHBlckxheWVyKSB7XG4gICAgaWYgKGhlbHBlckxheWVyKSB7XG4gICAgICAvL3ByZXZlbnQgZXJyb3Igd2hlbiBgdGhpcy5fY3VycmVudFN0ZXBgIGluIHVuZGVmaW5lZFxuICAgICAgaWYgKCF0aGlzLl9pbnRyb0l0ZW1zW3RoaXMuX2N1cnJlbnRTdGVwXSkgcmV0dXJuO1xuXG4gICAgICB2YXIgY3VycmVudEVsZW1lbnQgID0gdGhpcy5faW50cm9JdGVtc1t0aGlzLl9jdXJyZW50U3RlcF0sXG4gICAgICAgICAgZWxlbWVudFBvc2l0aW9uID0gX2dldE9mZnNldChjdXJyZW50RWxlbWVudC5lbGVtZW50KSxcbiAgICAgICAgICB3aWR0aEhlaWdodFBhZGRpbmcgPSAxMDtcblxuICAgICAgaWYgKGN1cnJlbnRFbGVtZW50LnBvc2l0aW9uID09ICdmbG9hdGluZycpIHtcbiAgICAgICAgd2lkdGhIZWlnaHRQYWRkaW5nID0gMDtcbiAgICAgIH1cblxuICAgICAgLy9zZXQgbmV3IHBvc2l0aW9uIHRvIGhlbHBlciBsYXllclxuICAgICAgaGVscGVyTGF5ZXIuc2V0QXR0cmlidXRlKCdzdHlsZScsICd3aWR0aDogJyArIChlbGVtZW50UG9zaXRpb24ud2lkdGggICsgd2lkdGhIZWlnaHRQYWRkaW5nKSAgKyAncHg7ICcgK1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICdoZWlnaHQ6JyArIChlbGVtZW50UG9zaXRpb24uaGVpZ2h0ICsgd2lkdGhIZWlnaHRQYWRkaW5nKSAgKyAncHg7ICcgK1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICd0b3A6JyAgICArIChlbGVtZW50UG9zaXRpb24udG9wICAgIC0gNSkgICArICdweDsnICtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAnbGVmdDogJyAgKyAoZWxlbWVudFBvc2l0aW9uLmxlZnQgICAtIDUpICAgKyAncHg7Jyk7XG4gICAgfVxuICB9XG5cbiAgLyoqXG4gICAqIFNob3cgYW4gZWxlbWVudCBvbiB0aGUgcGFnZVxuICAgKlxuICAgKiBAYXBpIHByaXZhdGVcbiAgICogQG1ldGhvZCBfc2hvd0VsZW1lbnRcbiAgICogQHBhcmFtIHtPYmplY3R9IHRhcmdldEVsZW1lbnRcbiAgICovXG4gIGZ1bmN0aW9uIF9zaG93RWxlbWVudCh0YXJnZXRFbGVtZW50KSB7XG5cbiAgICBpZiAodHlwZW9mICh0aGlzLl9pbnRyb0NoYW5nZUNhbGxiYWNrKSAhPT0gJ3VuZGVmaW5lZCcpIHtcbiAgICAgICAgdGhpcy5faW50cm9DaGFuZ2VDYWxsYmFjay5jYWxsKHRoaXMsIHRhcmdldEVsZW1lbnQuZWxlbWVudCk7XG4gICAgfVxuXG4gICAgdmFyIHNlbGYgPSB0aGlzLFxuICAgICAgICBvbGRIZWxwZXJMYXllciA9IGRvY3VtZW50LnF1ZXJ5U2VsZWN0b3IoJy5pbnRyb2pzLWhlbHBlckxheWVyJyksXG4gICAgICAgIGVsZW1lbnRQb3NpdGlvbiA9IF9nZXRPZmZzZXQodGFyZ2V0RWxlbWVudC5lbGVtZW50KTtcblxuICAgIGlmIChvbGRIZWxwZXJMYXllciAhPSBudWxsKSB7XG4gICAgICB2YXIgb2xkSGVscGVyTnVtYmVyTGF5ZXIgPSBvbGRIZWxwZXJMYXllci5xdWVyeVNlbGVjdG9yKCcuaW50cm9qcy1oZWxwZXJOdW1iZXJMYXllcicpLFxuICAgICAgICAgIG9sZHRvb2x0aXBMYXllciAgICAgID0gb2xkSGVscGVyTGF5ZXIucXVlcnlTZWxlY3RvcignLmludHJvanMtdG9vbHRpcHRleHQnKSxcbiAgICAgICAgICBvbGRBcnJvd0xheWVyICAgICAgICA9IG9sZEhlbHBlckxheWVyLnF1ZXJ5U2VsZWN0b3IoJy5pbnRyb2pzLWFycm93JyksXG4gICAgICAgICAgb2xkdG9vbHRpcENvbnRhaW5lciAgPSBvbGRIZWxwZXJMYXllci5xdWVyeVNlbGVjdG9yKCcuaW50cm9qcy10b29sdGlwJyksXG4gICAgICAgICAgc2tpcFRvb2x0aXBCdXR0b24gICAgPSBvbGRIZWxwZXJMYXllci5xdWVyeVNlbGVjdG9yKCcuaW50cm9qcy1za2lwYnV0dG9uJyksXG4gICAgICAgICAgcHJldlRvb2x0aXBCdXR0b24gICAgPSBvbGRIZWxwZXJMYXllci5xdWVyeVNlbGVjdG9yKCcuaW50cm9qcy1wcmV2YnV0dG9uJyksXG4gICAgICAgICAgbmV4dFRvb2x0aXBCdXR0b24gICAgPSBvbGRIZWxwZXJMYXllci5xdWVyeVNlbGVjdG9yKCcuaW50cm9qcy1uZXh0YnV0dG9uJyk7XG5cbiAgICAgIC8vaGlkZSB0aGUgdG9vbHRpcFxuICAgICAgb2xkdG9vbHRpcENvbnRhaW5lci5zdHlsZS5vcGFjaXR5ID0gMDtcblxuICAgICAgaWYgKG9sZEhlbHBlck51bWJlckxheWVyICE9IG51bGwpIHtcbiAgICAgICAgdmFyIGxhc3RJbnRyb0l0ZW0gPSB0aGlzLl9pbnRyb0l0ZW1zWyh0YXJnZXRFbGVtZW50LnN0ZXAgLSAyID49IDAgPyB0YXJnZXRFbGVtZW50LnN0ZXAgLSAyIDogMCldO1xuXG4gICAgICAgIGlmIChsYXN0SW50cm9JdGVtICE9IG51bGwgJiYgKHRoaXMuX2RpcmVjdGlvbiA9PSAnZm9yd2FyZCcgJiYgbGFzdEludHJvSXRlbS5wb3NpdGlvbiA9PSAnZmxvYXRpbmcnKSB8fCAodGhpcy5fZGlyZWN0aW9uID09ICdiYWNrd2FyZCcgJiYgdGFyZ2V0RWxlbWVudC5wb3NpdGlvbiA9PSAnZmxvYXRpbmcnKSkge1xuICAgICAgICAgIG9sZEhlbHBlck51bWJlckxheWVyLnN0eWxlLm9wYWNpdHkgPSAwO1xuICAgICAgICB9XG4gICAgICB9XG5cbiAgICAgIC8vc2V0IG5ldyBwb3NpdGlvbiB0byBoZWxwZXIgbGF5ZXJcbiAgICAgIF9zZXRIZWxwZXJMYXllclBvc2l0aW9uLmNhbGwoc2VsZiwgb2xkSGVscGVyTGF5ZXIpO1xuXG4gICAgICAvL3JlbW92ZSBgaW50cm9qcy1maXhQYXJlbnRgIGNsYXNzIGZyb20gdGhlIGVsZW1lbnRzXG4gICAgICB2YXIgZml4UGFyZW50cyA9IGRvY3VtZW50LnF1ZXJ5U2VsZWN0b3JBbGwoJy5pbnRyb2pzLWZpeFBhcmVudCcpO1xuICAgICAgaWYgKGZpeFBhcmVudHMgJiYgZml4UGFyZW50cy5sZW5ndGggPiAwKSB7XG4gICAgICAgIGZvciAodmFyIGkgPSBmaXhQYXJlbnRzLmxlbmd0aCAtIDE7IGkgPj0gMDsgaS0tKSB7XG4gICAgICAgICAgZml4UGFyZW50c1tpXS5jbGFzc05hbWUgPSBmaXhQYXJlbnRzW2ldLmNsYXNzTmFtZS5yZXBsYWNlKC9pbnRyb2pzLWZpeFBhcmVudC9nLCAnJykucmVwbGFjZSgvXlxccyt8XFxzKyQvZywgJycpO1xuICAgICAgICB9O1xuICAgICAgfVxuXG4gICAgICAvL3JlbW92ZSBvbGQgY2xhc3Nlc1xuICAgICAgdmFyIG9sZFNob3dFbGVtZW50ID0gZG9jdW1lbnQucXVlcnlTZWxlY3RvcignLmludHJvanMtc2hvd0VsZW1lbnQnKTtcbiAgICAgIG9sZFNob3dFbGVtZW50LmNsYXNzTmFtZSA9IG9sZFNob3dFbGVtZW50LmNsYXNzTmFtZS5yZXBsYWNlKC9pbnRyb2pzLVthLXpBLVpdKy9nLCAnJykucmVwbGFjZSgvXlxccyt8XFxzKyQvZywgJycpO1xuICAgICAgLy93ZSBzaG91bGQgd2FpdCB1bnRpbCB0aGUgQ1NTMyB0cmFuc2l0aW9uIGlzIGNvbXBldGVkIChpdCdzIDAuMyBzZWMpIHRvIHByZXZlbnQgaW5jb3JyZWN0IGBoZWlnaHRgIGFuZCBgd2lkdGhgIGNhbGN1bGF0aW9uXG4gICAgICBpZiAoc2VsZi5fbGFzdFNob3dFbGVtZW50VGltZXIpIHtcbiAgICAgICAgY2xlYXJUaW1lb3V0KHNlbGYuX2xhc3RTaG93RWxlbWVudFRpbWVyKTtcbiAgICAgIH1cbiAgICAgIHNlbGYuX2xhc3RTaG93RWxlbWVudFRpbWVyID0gc2V0VGltZW91dChmdW5jdGlvbigpIHtcbiAgICAgICAgLy9zZXQgY3VycmVudCBzdGVwIHRvIHRoZSBsYWJlbFxuICAgICAgICBpZiAob2xkSGVscGVyTnVtYmVyTGF5ZXIgIT0gbnVsbCkge1xuICAgICAgICAgIG9sZEhlbHBlck51bWJlckxheWVyLmlubmVySFRNTCA9IHRhcmdldEVsZW1lbnQuc3RlcDtcbiAgICAgICAgfVxuICAgICAgICAvL3NldCBjdXJyZW50IHRvb2x0aXAgdGV4dFxuICAgICAgICBvbGR0b29sdGlwTGF5ZXIuaW5uZXJIVE1MID0gdGFyZ2V0RWxlbWVudC5pbnRybztcbiAgICAgICAgLy9zZXQgdGhlIHRvb2x0aXAgcG9zaXRpb25cbiAgICAgICAgX3BsYWNlVG9vbHRpcC5jYWxsKHNlbGYsIHRhcmdldEVsZW1lbnQuZWxlbWVudCwgb2xkdG9vbHRpcENvbnRhaW5lciwgb2xkQXJyb3dMYXllciwgb2xkSGVscGVyTnVtYmVyTGF5ZXIpO1xuXG4gICAgICAgIC8vY2hhbmdlIGFjdGl2ZSBidWxsZXRcbiAgICAgICAgb2xkSGVscGVyTGF5ZXIucXVlcnlTZWxlY3RvcignLmludHJvanMtYnVsbGV0cyBsaSA+IGEuYWN0aXZlJykuY2xhc3NOYW1lID0gJyc7XG4gICAgICAgIG9sZEhlbHBlckxheWVyLnF1ZXJ5U2VsZWN0b3IoJy5pbnRyb2pzLWJ1bGxldHMgbGkgPiBhW2RhdGEtc3RlcG51bWJlcj1cIicgKyB0YXJnZXRFbGVtZW50LnN0ZXAgKyAnXCJdJykuY2xhc3NOYW1lID0gJ2FjdGl2ZSc7XG5cbiAgICAgICAgLy9zaG93IHRoZSB0b29sdGlwXG4gICAgICAgIG9sZHRvb2x0aXBDb250YWluZXIuc3R5bGUub3BhY2l0eSA9IDE7XG4gICAgICAgIGlmIChvbGRIZWxwZXJOdW1iZXJMYXllcikgb2xkSGVscGVyTnVtYmVyTGF5ZXIuc3R5bGUub3BhY2l0eSA9IDE7XG4gICAgICB9LCAzNTApO1xuXG4gICAgfSBlbHNlIHtcbiAgICAgIHZhciBoZWxwZXJMYXllciAgICAgICA9IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoJ2RpdicpLFxuICAgICAgICAgIGFycm93TGF5ZXIgICAgICAgID0gZG9jdW1lbnQuY3JlYXRlRWxlbWVudCgnZGl2JyksXG4gICAgICAgICAgdG9vbHRpcExheWVyICAgICAgPSBkb2N1bWVudC5jcmVhdGVFbGVtZW50KCdkaXYnKSxcbiAgICAgICAgICB0b29sdGlwVGV4dExheWVyICA9IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoJ2RpdicpLFxuICAgICAgICAgIGJ1bGxldHNMYXllciAgICAgID0gZG9jdW1lbnQuY3JlYXRlRWxlbWVudCgnZGl2JyksXG4gICAgICAgICAgYnV0dG9uc0xheWVyICAgICAgPSBkb2N1bWVudC5jcmVhdGVFbGVtZW50KCdkaXYnKTtcblxuICAgICAgaGVscGVyTGF5ZXIuY2xhc3NOYW1lID0gJ2ludHJvanMtaGVscGVyTGF5ZXInO1xuXG4gICAgICAvL3NldCBuZXcgcG9zaXRpb24gdG8gaGVscGVyIGxheWVyXG4gICAgICBfc2V0SGVscGVyTGF5ZXJQb3NpdGlvbi5jYWxsKHNlbGYsIGhlbHBlckxheWVyKTtcblxuICAgICAgLy9hZGQgaGVscGVyIGxheWVyIHRvIHRhcmdldCBlbGVtZW50XG4gICAgICB0aGlzLl90YXJnZXRFbGVtZW50LmFwcGVuZENoaWxkKGhlbHBlckxheWVyKTtcblxuICAgICAgYXJyb3dMYXllci5jbGFzc05hbWUgPSAnaW50cm9qcy1hcnJvdyc7XG5cbiAgICAgIHRvb2x0aXBUZXh0TGF5ZXIuY2xhc3NOYW1lID0gJ2ludHJvanMtdG9vbHRpcHRleHQnO1xuICAgICAgdG9vbHRpcFRleHRMYXllci5pbm5lckhUTUwgPSB0YXJnZXRFbGVtZW50LmludHJvO1xuXG4gICAgICBidWxsZXRzTGF5ZXIuY2xhc3NOYW1lID0gJ2ludHJvanMtYnVsbGV0cyc7XG5cbiAgICAgIGlmICh0aGlzLl9vcHRpb25zLnNob3dCdWxsZXRzID09PSBmYWxzZSkge1xuICAgICAgICBidWxsZXRzTGF5ZXIuc3R5bGUuZGlzcGxheSA9ICdub25lJztcbiAgICAgIH1cblxuICAgICAgdmFyIHVsQ29udGFpbmVyID0gZG9jdW1lbnQuY3JlYXRlRWxlbWVudCgndWwnKTtcblxuICAgICAgZm9yICh2YXIgaSA9IDAsIHN0ZXBzTGVuZ3RoID0gdGhpcy5faW50cm9JdGVtcy5sZW5ndGg7IGkgPCBzdGVwc0xlbmd0aDsgaSsrKSB7XG4gICAgICAgIHZhciBpbm5lckxpICAgID0gZG9jdW1lbnQuY3JlYXRlRWxlbWVudCgnbGknKTtcbiAgICAgICAgdmFyIGFuY2hvckxpbmsgPSBkb2N1bWVudC5jcmVhdGVFbGVtZW50KCdhJyk7XG5cbiAgICAgICAgYW5jaG9yTGluay5vbmNsaWNrID0gZnVuY3Rpb24oKSB7XG4gICAgICAgICAgc2VsZi5nb1RvU3RlcCh0aGlzLmdldEF0dHJpYnV0ZSgnZGF0YS1zdGVwbnVtYmVyJykpO1xuICAgICAgICB9O1xuXG4gICAgICAgIGlmIChpID09PSAwKSBhbmNob3JMaW5rLmNsYXNzTmFtZSA9IFwiYWN0aXZlXCI7XG5cbiAgICAgICAgYW5jaG9yTGluay5ocmVmID0gJ2phdmFzY3JpcHQ6dm9pZCgwKTsnO1xuICAgICAgICBhbmNob3JMaW5rLmlubmVySFRNTCA9IFwiJm5ic3A7XCI7XG4gICAgICAgIGFuY2hvckxpbmsuc2V0QXR0cmlidXRlKCdkYXRhLXN0ZXBudW1iZXInLCB0aGlzLl9pbnRyb0l0ZW1zW2ldLnN0ZXApO1xuXG4gICAgICAgIGlubmVyTGkuYXBwZW5kQ2hpbGQoYW5jaG9yTGluayk7XG4gICAgICAgIHVsQ29udGFpbmVyLmFwcGVuZENoaWxkKGlubmVyTGkpO1xuICAgICAgfVxuXG4gICAgICBidWxsZXRzTGF5ZXIuYXBwZW5kQ2hpbGQodWxDb250YWluZXIpO1xuXG4gICAgICBidXR0b25zTGF5ZXIuY2xhc3NOYW1lID0gJ2ludHJvanMtdG9vbHRpcGJ1dHRvbnMnO1xuICAgICAgaWYgKHRoaXMuX29wdGlvbnMuc2hvd0J1dHRvbnMgPT09IGZhbHNlKSB7XG4gICAgICAgIGJ1dHRvbnNMYXllci5zdHlsZS5kaXNwbGF5ID0gJ25vbmUnO1xuICAgICAgfVxuXG4gICAgICB0b29sdGlwTGF5ZXIuY2xhc3NOYW1lID0gJ2ludHJvanMtdG9vbHRpcCc7XG4gICAgICB0b29sdGlwTGF5ZXIuYXBwZW5kQ2hpbGQodG9vbHRpcFRleHRMYXllcik7XG4gICAgICB0b29sdGlwTGF5ZXIuYXBwZW5kQ2hpbGQoYnVsbGV0c0xheWVyKTtcblxuICAgICAgLy9hZGQgaGVscGVyIGxheWVyIG51bWJlclxuICAgICAgaWYgKHRoaXMuX29wdGlvbnMuc2hvd1N0ZXBOdW1iZXJzID09IHRydWUpIHtcbiAgICAgICAgdmFyIGhlbHBlck51bWJlckxheWVyID0gZG9jdW1lbnQuY3JlYXRlRWxlbWVudCgnc3BhbicpO1xuICAgICAgICBoZWxwZXJOdW1iZXJMYXllci5jbGFzc05hbWUgPSAnaW50cm9qcy1oZWxwZXJOdW1iZXJMYXllcic7XG4gICAgICAgIGhlbHBlck51bWJlckxheWVyLmlubmVySFRNTCA9IHRhcmdldEVsZW1lbnQuc3RlcDtcbiAgICAgICAgaGVscGVyTGF5ZXIuYXBwZW5kQ2hpbGQoaGVscGVyTnVtYmVyTGF5ZXIpO1xuICAgICAgfVxuICAgICAgdG9vbHRpcExheWVyLmFwcGVuZENoaWxkKGFycm93TGF5ZXIpO1xuICAgICAgaGVscGVyTGF5ZXIuYXBwZW5kQ2hpbGQodG9vbHRpcExheWVyKTtcblxuICAgICAgLy9uZXh0IGJ1dHRvblxuICAgICAgdmFyIG5leHRUb29sdGlwQnV0dG9uID0gZG9jdW1lbnQuY3JlYXRlRWxlbWVudCgnYScpO1xuXG4gICAgICBuZXh0VG9vbHRpcEJ1dHRvbi5vbmNsaWNrID0gZnVuY3Rpb24oKSB7XG4gICAgICAgIGlmIChzZWxmLl9pbnRyb0l0ZW1zLmxlbmd0aCAtIDEgIT0gc2VsZi5fY3VycmVudFN0ZXApIHtcbiAgICAgICAgICBfbmV4dFN0ZXAuY2FsbChzZWxmKTtcbiAgICAgICAgfVxuICAgICAgfTtcblxuICAgICAgbmV4dFRvb2x0aXBCdXR0b24uaHJlZiA9ICdqYXZhc2NyaXB0OnZvaWQoMCk7JztcbiAgICAgIG5leHRUb29sdGlwQnV0dG9uLmlubmVySFRNTCA9IHRoaXMuX29wdGlvbnMubmV4dExhYmVsO1xuXG4gICAgICAvL3ByZXZpb3VzIGJ1dHRvblxuICAgICAgdmFyIHByZXZUb29sdGlwQnV0dG9uID0gZG9jdW1lbnQuY3JlYXRlRWxlbWVudCgnYScpO1xuXG4gICAgICBwcmV2VG9vbHRpcEJ1dHRvbi5vbmNsaWNrID0gZnVuY3Rpb24oKSB7XG4gICAgICAgIGlmIChzZWxmLl9jdXJyZW50U3RlcCAhPSAwKSB7XG4gICAgICAgICAgX3ByZXZpb3VzU3RlcC5jYWxsKHNlbGYpO1xuICAgICAgICB9XG4gICAgICB9O1xuXG4gICAgICBwcmV2VG9vbHRpcEJ1dHRvbi5ocmVmID0gJ2phdmFzY3JpcHQ6dm9pZCgwKTsnO1xuICAgICAgcHJldlRvb2x0aXBCdXR0b24uaW5uZXJIVE1MID0gdGhpcy5fb3B0aW9ucy5wcmV2TGFiZWw7XG5cbiAgICAgIC8vc2tpcCBidXR0b25cbiAgICAgIHZhciBza2lwVG9vbHRpcEJ1dHRvbiA9IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoJ2EnKTtcbiAgICAgIHNraXBUb29sdGlwQnV0dG9uLmNsYXNzTmFtZSA9ICdpbnRyb2pzLWJ1dHRvbiBpbnRyb2pzLXNraXBidXR0b24nO1xuICAgICAgc2tpcFRvb2x0aXBCdXR0b24uaHJlZiA9ICdqYXZhc2NyaXB0OnZvaWQoMCk7JztcbiAgICAgIHNraXBUb29sdGlwQnV0dG9uLmlubmVySFRNTCA9IHRoaXMuX29wdGlvbnMuc2tpcExhYmVsO1xuXG4gICAgICBza2lwVG9vbHRpcEJ1dHRvbi5vbmNsaWNrID0gZnVuY3Rpb24oKSB7XG4gICAgICAgIGlmIChzZWxmLl9pbnRyb0l0ZW1zLmxlbmd0aCAtIDEgPT0gc2VsZi5fY3VycmVudFN0ZXAgJiYgdHlwZW9mIChzZWxmLl9pbnRyb0NvbXBsZXRlQ2FsbGJhY2spID09PSAnZnVuY3Rpb24nKSB7XG4gICAgICAgICAgc2VsZi5faW50cm9Db21wbGV0ZUNhbGxiYWNrLmNhbGwoc2VsZik7XG4gICAgICAgIH1cblxuICAgICAgICBpZiAoc2VsZi5faW50cm9JdGVtcy5sZW5ndGggLSAxICE9IHNlbGYuX2N1cnJlbnRTdGVwICYmIHR5cGVvZiAoc2VsZi5faW50cm9FeGl0Q2FsbGJhY2spID09PSAnZnVuY3Rpb24nKSB7XG4gICAgICAgICAgc2VsZi5faW50cm9FeGl0Q2FsbGJhY2suY2FsbChzZWxmKTtcbiAgICAgICAgfVxuXG4gICAgICAgIF9leGl0SW50cm8uY2FsbChzZWxmLCBzZWxmLl90YXJnZXRFbGVtZW50KTtcbiAgICAgIH07XG5cbiAgICAgIGJ1dHRvbnNMYXllci5hcHBlbmRDaGlsZChza2lwVG9vbHRpcEJ1dHRvbik7XG5cbiAgICAgIC8vaW4gb3JkZXIgdG8gcHJldmVudCBkaXNwbGF5aW5nIG5leHQvcHJldmlvdXMgYnV0dG9uIGFsd2F5c1xuICAgICAgaWYgKHRoaXMuX2ludHJvSXRlbXMubGVuZ3RoID4gMSkge1xuICAgICAgICBidXR0b25zTGF5ZXIuYXBwZW5kQ2hpbGQocHJldlRvb2x0aXBCdXR0b24pO1xuICAgICAgICBidXR0b25zTGF5ZXIuYXBwZW5kQ2hpbGQobmV4dFRvb2x0aXBCdXR0b24pO1xuICAgICAgfVxuXG4gICAgICB0b29sdGlwTGF5ZXIuYXBwZW5kQ2hpbGQoYnV0dG9uc0xheWVyKTtcblxuICAgICAgLy9zZXQgcHJvcGVyIHBvc2l0aW9uXG4gICAgICBfcGxhY2VUb29sdGlwLmNhbGwoc2VsZiwgdGFyZ2V0RWxlbWVudC5lbGVtZW50LCB0b29sdGlwTGF5ZXIsIGFycm93TGF5ZXIsIGhlbHBlck51bWJlckxheWVyKTtcbiAgICB9XG5cbiAgICBpZiAodGhpcy5fY3VycmVudFN0ZXAgPT0gMCAmJiB0aGlzLl9pbnRyb0l0ZW1zLmxlbmd0aCA+IDEpIHtcbiAgICAgIHByZXZUb29sdGlwQnV0dG9uLmNsYXNzTmFtZSA9ICdpbnRyb2pzLWJ1dHRvbiBpbnRyb2pzLXByZXZidXR0b24gaW50cm9qcy1kaXNhYmxlZCc7XG4gICAgICBuZXh0VG9vbHRpcEJ1dHRvbi5jbGFzc05hbWUgPSAnaW50cm9qcy1idXR0b24gaW50cm9qcy1uZXh0YnV0dG9uJztcbiAgICAgIHNraXBUb29sdGlwQnV0dG9uLmlubmVySFRNTCA9IHRoaXMuX29wdGlvbnMuc2tpcExhYmVsO1xuICAgIH0gZWxzZSBpZiAodGhpcy5faW50cm9JdGVtcy5sZW5ndGggLSAxID09IHRoaXMuX2N1cnJlbnRTdGVwIHx8IHRoaXMuX2ludHJvSXRlbXMubGVuZ3RoID09IDEpIHtcbiAgICAgIHNraXBUb29sdGlwQnV0dG9uLmlubmVySFRNTCA9IHRoaXMuX29wdGlvbnMuZG9uZUxhYmVsO1xuICAgICAgcHJldlRvb2x0aXBCdXR0b24uY2xhc3NOYW1lID0gJ2ludHJvanMtYnV0dG9uIGludHJvanMtcHJldmJ1dHRvbic7XG4gICAgICBuZXh0VG9vbHRpcEJ1dHRvbi5jbGFzc05hbWUgPSAnaW50cm9qcy1idXR0b24gaW50cm9qcy1uZXh0YnV0dG9uIGludHJvanMtZGlzYWJsZWQnO1xuICAgIH0gZWxzZSB7XG4gICAgICBwcmV2VG9vbHRpcEJ1dHRvbi5jbGFzc05hbWUgPSAnaW50cm9qcy1idXR0b24gaW50cm9qcy1wcmV2YnV0dG9uJztcbiAgICAgIG5leHRUb29sdGlwQnV0dG9uLmNsYXNzTmFtZSA9ICdpbnRyb2pzLWJ1dHRvbiBpbnRyb2pzLW5leHRidXR0b24nO1xuICAgICAgc2tpcFRvb2x0aXBCdXR0b24uaW5uZXJIVE1MID0gdGhpcy5fb3B0aW9ucy5za2lwTGFiZWw7XG4gICAgfVxuXG4gICAgLy9TZXQgZm9jdXMgb24gXCJuZXh0XCIgYnV0dG9uLCBzbyB0aGF0IGhpdHRpbmcgRW50ZXIgYWx3YXlzIG1vdmVzIHlvdSBvbnRvIHRoZSBuZXh0IHN0ZXBcbiAgICBuZXh0VG9vbHRpcEJ1dHRvbi5mb2N1cygpO1xuXG4gICAgLy9hZGQgdGFyZ2V0IGVsZW1lbnQgcG9zaXRpb24gc3R5bGVcbiAgICB0YXJnZXRFbGVtZW50LmVsZW1lbnQuY2xhc3NOYW1lICs9ICcgaW50cm9qcy1zaG93RWxlbWVudCc7XG5cbiAgICB2YXIgY3VycmVudEVsZW1lbnRQb3NpdGlvbiA9IF9nZXRQcm9wVmFsdWUodGFyZ2V0RWxlbWVudC5lbGVtZW50LCAncG9zaXRpb24nKTtcbiAgICBpZiAoY3VycmVudEVsZW1lbnRQb3NpdGlvbiAhPT0gJ2Fic29sdXRlJyAmJlxuICAgICAgICBjdXJyZW50RWxlbWVudFBvc2l0aW9uICE9PSAncmVsYXRpdmUnKSB7XG4gICAgICAvL2NoYW5nZSB0byBuZXcgaW50cm8gaXRlbVxuICAgICAgdGFyZ2V0RWxlbWVudC5lbGVtZW50LmNsYXNzTmFtZSArPSAnIGludHJvanMtcmVsYXRpdmVQb3NpdGlvbic7XG4gICAgfVxuXG4gICAgdmFyIHBhcmVudEVsbSA9IHRhcmdldEVsZW1lbnQuZWxlbWVudC5wYXJlbnROb2RlO1xuICAgIHdoaWxlIChwYXJlbnRFbG0gIT0gbnVsbCkge1xuICAgICAgaWYgKHBhcmVudEVsbS50YWdOYW1lLnRvTG93ZXJDYXNlKCkgPT09ICdib2R5JykgYnJlYWs7XG5cbiAgICAgIC8vZml4IFRoZSBTdGFja2luZyBDb250ZW54dCBwcm9ibGVtLlxuICAgICAgLy9Nb3JlIGRldGFpbDogaHR0cHM6Ly9kZXZlbG9wZXIubW96aWxsYS5vcmcvZW4tVVMvZG9jcy9XZWIvR3VpZGUvQ1NTL1VuZGVyc3RhbmRpbmdfel9pbmRleC9UaGVfc3RhY2tpbmdfY29udGV4dFxuICAgICAgdmFyIHpJbmRleCA9IF9nZXRQcm9wVmFsdWUocGFyZW50RWxtLCAnei1pbmRleCcpO1xuICAgICAgdmFyIG9wYWNpdHkgPSBwYXJzZUZsb2F0KF9nZXRQcm9wVmFsdWUocGFyZW50RWxtLCAnb3BhY2l0eScpKTtcbiAgICAgIGlmICgvWzAtOV0rLy50ZXN0KHpJbmRleCkgfHwgb3BhY2l0eSA8IDEpIHtcbiAgICAgICAgcGFyZW50RWxtLmNsYXNzTmFtZSArPSAnIGludHJvanMtZml4UGFyZW50JztcbiAgICAgIH1cblxuICAgICAgcGFyZW50RWxtID0gcGFyZW50RWxtLnBhcmVudE5vZGU7XG4gICAgfVxuXG4gICAgaWYgKCFfZWxlbWVudEluVmlld3BvcnQodGFyZ2V0RWxlbWVudC5lbGVtZW50KSAmJiB0aGlzLl9vcHRpb25zLnNjcm9sbFRvRWxlbWVudCA9PT0gdHJ1ZSkge1xuICAgICAgdmFyIHJlY3QgPSB0YXJnZXRFbGVtZW50LmVsZW1lbnQuZ2V0Qm91bmRpbmdDbGllbnRSZWN0KCksXG4gICAgICAgIHdpbkhlaWdodD1fZ2V0V2luU2l6ZSgpLmhlaWdodCxcbiAgICAgICAgdG9wID0gcmVjdC5ib3R0b20gLSAocmVjdC5ib3R0b20gLSByZWN0LnRvcCksXG4gICAgICAgIGJvdHRvbSA9IHJlY3QuYm90dG9tIC0gd2luSGVpZ2h0O1xuXG4gICAgICAvL1Njcm9sbCB1cFxuICAgICAgaWYgKHRvcCA8IDAgfHwgdGFyZ2V0RWxlbWVudC5lbGVtZW50LmNsaWVudEhlaWdodCA+IHdpbkhlaWdodCkge1xuICAgICAgICB3aW5kb3cuc2Nyb2xsQnkoMCwgdG9wIC0gMzApOyAvLyAzMHB4IHBhZGRpbmcgZnJvbSBlZGdlIHRvIGxvb2sgbmljZVxuXG4gICAgICAvL1Njcm9sbCBkb3duXG4gICAgICB9IGVsc2Uge1xuICAgICAgICB3aW5kb3cuc2Nyb2xsQnkoMCwgYm90dG9tICsgMTAwKTsgLy8gNzBweCArIDMwcHggcGFkZGluZyBmcm9tIGVkZ2UgdG8gbG9vayBuaWNlXG4gICAgICB9XG4gICAgfVxuXG4gICAgaWYgKHR5cGVvZiAodGhpcy5faW50cm9BZnRlckNoYW5nZUNhbGxiYWNrKSAhPT0gJ3VuZGVmaW5lZCcpIHtcbiAgICAgICAgdGhpcy5faW50cm9BZnRlckNoYW5nZUNhbGxiYWNrLmNhbGwodGhpcywgdGFyZ2V0RWxlbWVudC5lbGVtZW50KTtcbiAgICB9XG4gIH1cblxuICAvKipcbiAgICogR2V0IGFuIGVsZW1lbnQgQ1NTIHByb3BlcnR5IG9uIHRoZSBwYWdlXG4gICAqIFRoYW5rcyB0byBKYXZhU2NyaXB0IEtpdDogaHR0cDovL3d3dy5qYXZhc2NyaXB0a2l0LmNvbS9kaHRtbHR1dG9ycy9kaHRtbGNhc2NhZGU0LnNodG1sXG4gICAqXG4gICAqIEBhcGkgcHJpdmF0ZVxuICAgKiBAbWV0aG9kIF9nZXRQcm9wVmFsdWVcbiAgICogQHBhcmFtIHtPYmplY3R9IGVsZW1lbnRcbiAgICogQHBhcmFtIHtTdHJpbmd9IHByb3BOYW1lXG4gICAqIEByZXR1cm5zIEVsZW1lbnQncyBwcm9wZXJ0eSB2YWx1ZVxuICAgKi9cbiAgZnVuY3Rpb24gX2dldFByb3BWYWx1ZSAoZWxlbWVudCwgcHJvcE5hbWUpIHtcbiAgICB2YXIgcHJvcFZhbHVlID0gJyc7XG4gICAgaWYgKGVsZW1lbnQuY3VycmVudFN0eWxlKSB7IC8vSUVcbiAgICAgIHByb3BWYWx1ZSA9IGVsZW1lbnQuY3VycmVudFN0eWxlW3Byb3BOYW1lXTtcbiAgICB9IGVsc2UgaWYgKGRvY3VtZW50LmRlZmF1bHRWaWV3ICYmIGRvY3VtZW50LmRlZmF1bHRWaWV3LmdldENvbXB1dGVkU3R5bGUpIHsgLy9PdGhlcnNcbiAgICAgIHByb3BWYWx1ZSA9IGRvY3VtZW50LmRlZmF1bHRWaWV3LmdldENvbXB1dGVkU3R5bGUoZWxlbWVudCwgbnVsbCkuZ2V0UHJvcGVydHlWYWx1ZShwcm9wTmFtZSk7XG4gICAgfVxuXG4gICAgLy9QcmV2ZW50IGV4Y2VwdGlvbiBpbiBJRVxuICAgIGlmIChwcm9wVmFsdWUgJiYgcHJvcFZhbHVlLnRvTG93ZXJDYXNlKSB7XG4gICAgICByZXR1cm4gcHJvcFZhbHVlLnRvTG93ZXJDYXNlKCk7XG4gICAgfSBlbHNlIHtcbiAgICAgIHJldHVybiBwcm9wVmFsdWU7XG4gICAgfVxuICB9XG5cbiAgLyoqXG4gICAqIFByb3ZpZGVzIGEgY3Jvc3MtYnJvd3NlciB3YXkgdG8gZ2V0IHRoZSBzY3JlZW4gZGltZW5zaW9uc1xuICAgKiB2aWE6IGh0dHA6Ly9zdGFja292ZXJmbG93LmNvbS9xdWVzdGlvbnMvNTg2NDQ2Ny9pbnRlcm5ldC1leHBsb3Jlci1pbm5lcmhlaWdodFxuICAgKlxuICAgKiBAYXBpIHByaXZhdGVcbiAgICogQG1ldGhvZCBfZ2V0V2luU2l6ZVxuICAgKiBAcmV0dXJucyB7T2JqZWN0fSB3aWR0aCBhbmQgaGVpZ2h0IGF0dHJpYnV0ZXNcbiAgICovXG4gIGZ1bmN0aW9uIF9nZXRXaW5TaXplKCkge1xuICAgIGlmICh3aW5kb3cuaW5uZXJXaWR0aCAhPSB1bmRlZmluZWQpIHtcbiAgICAgIHJldHVybiB7IHdpZHRoOiB3aW5kb3cuaW5uZXJXaWR0aCwgaGVpZ2h0OiB3aW5kb3cuaW5uZXJIZWlnaHQgfTtcbiAgICB9IGVsc2Uge1xuICAgICAgdmFyIEQgPSBkb2N1bWVudC5kb2N1bWVudEVsZW1lbnQ7XG4gICAgICByZXR1cm4geyB3aWR0aDogRC5jbGllbnRXaWR0aCwgaGVpZ2h0OiBELmNsaWVudEhlaWdodCB9O1xuICAgIH1cbiAgfVxuXG4gIC8qKlxuICAgKiBBZGQgb3ZlcmxheSBsYXllciB0byB0aGUgcGFnZVxuICAgKiBodHRwOi8vc3RhY2tvdmVyZmxvdy5jb20vcXVlc3Rpb25zLzEyMzk5OS9ob3ctdG8tdGVsbC1pZi1hLWRvbS1lbGVtZW50LWlzLXZpc2libGUtaW4tdGhlLWN1cnJlbnQtdmlld3BvcnRcbiAgICpcbiAgICogQGFwaSBwcml2YXRlXG4gICAqIEBtZXRob2QgX2VsZW1lbnRJblZpZXdwb3J0XG4gICAqIEBwYXJhbSB7T2JqZWN0fSBlbFxuICAgKi9cbiAgZnVuY3Rpb24gX2VsZW1lbnRJblZpZXdwb3J0KGVsKSB7XG4gICAgdmFyIHJlY3QgPSBlbC5nZXRCb3VuZGluZ0NsaWVudFJlY3QoKTtcblxuICAgIHJldHVybiAoXG4gICAgICByZWN0LnRvcCA+PSAwICYmXG4gICAgICByZWN0LmxlZnQgPj0gMCAmJlxuICAgICAgKHJlY3QuYm90dG9tKzgwKSA8PSB3aW5kb3cuaW5uZXJIZWlnaHQgJiYgLy8gYWRkIDgwIHRvIGdldCB0aGUgdGV4dCByaWdodFxuICAgICAgcmVjdC5yaWdodCA8PSB3aW5kb3cuaW5uZXJXaWR0aFxuICAgICk7XG4gIH1cblxuICAvKipcbiAgICogQWRkIG92ZXJsYXkgbGF5ZXIgdG8gdGhlIHBhZ2VcbiAgICpcbiAgICogQGFwaSBwcml2YXRlXG4gICAqIEBtZXRob2QgX2FkZE92ZXJsYXlMYXllclxuICAgKiBAcGFyYW0ge09iamVjdH0gdGFyZ2V0RWxtXG4gICAqL1xuICBmdW5jdGlvbiBfYWRkT3ZlcmxheUxheWVyKHRhcmdldEVsbSkge1xuICAgIHZhciBvdmVybGF5TGF5ZXIgPSBkb2N1bWVudC5jcmVhdGVFbGVtZW50KCdkaXYnKSxcbiAgICAgICAgc3R5bGVUZXh0ID0gJycsXG4gICAgICAgIHNlbGYgPSB0aGlzO1xuXG4gICAgLy9zZXQgY3NzIGNsYXNzIG5hbWVcbiAgICBvdmVybGF5TGF5ZXIuY2xhc3NOYW1lID0gJ2ludHJvanMtb3ZlcmxheSc7XG5cbiAgICAvL2NoZWNrIGlmIHRoZSB0YXJnZXQgZWxlbWVudCBpcyBib2R5LCB3ZSBzaG91bGQgY2FsY3VsYXRlIHRoZSBzaXplIG9mIG92ZXJsYXkgbGF5ZXIgaW4gYSBiZXR0ZXIgd2F5XG4gICAgaWYgKHRhcmdldEVsbS50YWdOYW1lLnRvTG93ZXJDYXNlKCkgPT09ICdib2R5Jykge1xuICAgICAgc3R5bGVUZXh0ICs9ICd0b3A6IDA7Ym90dG9tOiAwOyBsZWZ0OiAwO3JpZ2h0OiAwO3Bvc2l0aW9uOiBmaXhlZDsnO1xuICAgICAgb3ZlcmxheUxheWVyLnNldEF0dHJpYnV0ZSgnc3R5bGUnLCBzdHlsZVRleHQpO1xuICAgIH0gZWxzZSB7XG4gICAgICAvL3NldCBvdmVybGF5IGxheWVyIHBvc2l0aW9uXG4gICAgICB2YXIgZWxlbWVudFBvc2l0aW9uID0gX2dldE9mZnNldCh0YXJnZXRFbG0pO1xuICAgICAgaWYgKGVsZW1lbnRQb3NpdGlvbikge1xuICAgICAgICBzdHlsZVRleHQgKz0gJ3dpZHRoOiAnICsgZWxlbWVudFBvc2l0aW9uLndpZHRoICsgJ3B4OyBoZWlnaHQ6JyArIGVsZW1lbnRQb3NpdGlvbi5oZWlnaHQgKyAncHg7IHRvcDonICsgZWxlbWVudFBvc2l0aW9uLnRvcCArICdweDtsZWZ0OiAnICsgZWxlbWVudFBvc2l0aW9uLmxlZnQgKyAncHg7JztcbiAgICAgICAgb3ZlcmxheUxheWVyLnNldEF0dHJpYnV0ZSgnc3R5bGUnLCBzdHlsZVRleHQpO1xuICAgICAgfVxuICAgIH1cblxuICAgIHRhcmdldEVsbS5hcHBlbmRDaGlsZChvdmVybGF5TGF5ZXIpO1xuXG4gICAgb3ZlcmxheUxheWVyLm9uY2xpY2sgPSBmdW5jdGlvbigpIHtcbiAgICAgIGlmIChzZWxmLl9vcHRpb25zLmV4aXRPbk92ZXJsYXlDbGljayA9PSB0cnVlKSB7XG4gICAgICAgIF9leGl0SW50cm8uY2FsbChzZWxmLCB0YXJnZXRFbG0pO1xuXG4gICAgICAgIC8vY2hlY2sgaWYgYW55IGNhbGxiYWNrIGlzIGRlZmluZWRcbiAgICAgICAgaWYgKHNlbGYuX2ludHJvRXhpdENhbGxiYWNrICE9IHVuZGVmaW5lZCkge1xuICAgICAgICAgIHNlbGYuX2ludHJvRXhpdENhbGxiYWNrLmNhbGwoc2VsZik7XG4gICAgICAgIH1cbiAgICAgIH1cbiAgICB9O1xuXG4gICAgc2V0VGltZW91dChmdW5jdGlvbigpIHtcbiAgICAgIHN0eWxlVGV4dCArPSAnb3BhY2l0eTogJyArIHNlbGYuX29wdGlvbnMub3ZlcmxheU9wYWNpdHkudG9TdHJpbmcoKSArICc7JztcbiAgICAgIG92ZXJsYXlMYXllci5zZXRBdHRyaWJ1dGUoJ3N0eWxlJywgc3R5bGVUZXh0KTtcbiAgICB9LCAxMCk7XG5cbiAgICByZXR1cm4gdHJ1ZTtcbiAgfVxuXG4gIC8qKlxuICAgKiBHZXQgYW4gZWxlbWVudCBwb3NpdGlvbiBvbiB0aGUgcGFnZVxuICAgKiBUaGFua3MgdG8gYG1lb3V3YDogaHR0cDovL3N0YWNrb3ZlcmZsb3cuY29tL2EvNDQyNDc0LzM3NTk2NlxuICAgKlxuICAgKiBAYXBpIHByaXZhdGVcbiAgICogQG1ldGhvZCBfZ2V0T2Zmc2V0XG4gICAqIEBwYXJhbSB7T2JqZWN0fSBlbGVtZW50XG4gICAqIEByZXR1cm5zIEVsZW1lbnQncyBwb3NpdGlvbiBpbmZvXG4gICAqL1xuICBmdW5jdGlvbiBfZ2V0T2Zmc2V0KGVsZW1lbnQpIHtcbiAgICB2YXIgZWxlbWVudFBvc2l0aW9uID0ge307XG5cbiAgICAvL3NldCB3aWR0aFxuICAgIGVsZW1lbnRQb3NpdGlvbi53aWR0aCA9IGVsZW1lbnQub2Zmc2V0V2lkdGg7XG5cbiAgICAvL3NldCBoZWlnaHRcbiAgICBlbGVtZW50UG9zaXRpb24uaGVpZ2h0ID0gZWxlbWVudC5vZmZzZXRIZWlnaHQ7XG5cbiAgICAvL2NhbGN1bGF0ZSBlbGVtZW50IHRvcCBhbmQgbGVmdFxuICAgIHZhciBfeCA9IDA7XG4gICAgdmFyIF95ID0gMDtcbiAgICB3aGlsZSAoZWxlbWVudCAmJiAhaXNOYU4oZWxlbWVudC5vZmZzZXRMZWZ0KSAmJiAhaXNOYU4oZWxlbWVudC5vZmZzZXRUb3ApKSB7XG4gICAgICBfeCArPSBlbGVtZW50Lm9mZnNldExlZnQ7XG4gICAgICBfeSArPSBlbGVtZW50Lm9mZnNldFRvcDtcbiAgICAgIGVsZW1lbnQgPSBlbGVtZW50Lm9mZnNldFBhcmVudDtcbiAgICB9XG4gICAgLy9zZXQgdG9wXG4gICAgZWxlbWVudFBvc2l0aW9uLnRvcCA9IF95O1xuICAgIC8vc2V0IGxlZnRcbiAgICBlbGVtZW50UG9zaXRpb24ubGVmdCA9IF94O1xuXG4gICAgcmV0dXJuIGVsZW1lbnRQb3NpdGlvbjtcbiAgfVxuXG4gIC8qKlxuICAgKiBPdmVyd3JpdGVzIG9iajEncyB2YWx1ZXMgd2l0aCBvYmoyJ3MgYW5kIGFkZHMgb2JqMidzIGlmIG5vbiBleGlzdGVudCBpbiBvYmoxXG4gICAqIHZpYTogaHR0cDovL3N0YWNrb3ZlcmZsb3cuY29tL3F1ZXN0aW9ucy8xNzEyNTEvaG93LWNhbi1pLW1lcmdlLXByb3BlcnRpZXMtb2YtdHdvLWphdmFzY3JpcHQtb2JqZWN0cy1keW5hbWljYWxseVxuICAgKlxuICAgKiBAcGFyYW0gb2JqMVxuICAgKiBAcGFyYW0gb2JqMlxuICAgKiBAcmV0dXJucyBvYmozIGEgbmV3IG9iamVjdCBiYXNlZCBvbiBvYmoxIGFuZCBvYmoyXG4gICAqL1xuICBmdW5jdGlvbiBfbWVyZ2VPcHRpb25zKG9iajEsb2JqMikge1xuICAgIHZhciBvYmozID0ge307XG4gICAgZm9yICh2YXIgYXR0cm5hbWUgaW4gb2JqMSkgeyBvYmozW2F0dHJuYW1lXSA9IG9iajFbYXR0cm5hbWVdOyB9XG4gICAgZm9yICh2YXIgYXR0cm5hbWUgaW4gb2JqMikgeyBvYmozW2F0dHJuYW1lXSA9IG9iajJbYXR0cm5hbWVdOyB9XG4gICAgcmV0dXJuIG9iajM7XG4gIH1cblxuICB2YXIgaW50cm9KcyA9IGZ1bmN0aW9uICh0YXJnZXRFbG0pIHtcbiAgICBpZiAodHlwZW9mICh0YXJnZXRFbG0pID09PSAnb2JqZWN0Jykge1xuICAgICAgLy9PaywgY3JlYXRlIGEgbmV3IGluc3RhbmNlXG4gICAgICByZXR1cm4gbmV3IEludHJvSnModGFyZ2V0RWxtKTtcblxuICAgIH0gZWxzZSBpZiAodHlwZW9mICh0YXJnZXRFbG0pID09PSAnc3RyaW5nJykge1xuICAgICAgLy9zZWxlY3QgdGhlIHRhcmdldCBlbGVtZW50IHdpdGggcXVlcnkgc2VsZWN0b3JcbiAgICAgIHZhciB0YXJnZXRFbGVtZW50ID0gZG9jdW1lbnQucXVlcnlTZWxlY3Rvcih0YXJnZXRFbG0pO1xuXG4gICAgICBpZiAodGFyZ2V0RWxlbWVudCkge1xuICAgICAgICByZXR1cm4gbmV3IEludHJvSnModGFyZ2V0RWxlbWVudCk7XG4gICAgICB9IGVsc2Uge1xuICAgICAgICB0aHJvdyBuZXcgRXJyb3IoJ1RoZXJlIGlzIG5vIGVsZW1lbnQgd2l0aCBnaXZlbiBzZWxlY3Rvci4nKTtcbiAgICAgIH1cbiAgICB9IGVsc2Uge1xuICAgICAgcmV0dXJuIG5ldyBJbnRyb0pzKGRvY3VtZW50LmJvZHkpO1xuICAgIH1cbiAgfTtcblxuICAvKipcbiAgICogQ3VycmVudCBJbnRyb0pzIHZlcnNpb25cbiAgICpcbiAgICogQHByb3BlcnR5IHZlcnNpb25cbiAgICogQHR5cGUgU3RyaW5nXG4gICAqL1xuICBpbnRyb0pzLnZlcnNpb24gPSBWRVJTSU9OO1xuXG4gIC8vUHJvdG90eXBlXG4gIGludHJvSnMuZm4gPSBJbnRyb0pzLnByb3RvdHlwZSA9IHtcbiAgICBjbG9uZTogZnVuY3Rpb24gKCkge1xuICAgICAgcmV0dXJuIG5ldyBJbnRyb0pzKHRoaXMpO1xuICAgIH0sXG4gICAgc2V0T3B0aW9uOiBmdW5jdGlvbihvcHRpb24sIHZhbHVlKSB7XG4gICAgICB0aGlzLl9vcHRpb25zW29wdGlvbl0gPSB2YWx1ZTtcbiAgICAgIHJldHVybiB0aGlzO1xuICAgIH0sXG4gICAgc2V0T3B0aW9uczogZnVuY3Rpb24ob3B0aW9ucykge1xuICAgICAgdGhpcy5fb3B0aW9ucyA9IF9tZXJnZU9wdGlvbnModGhpcy5fb3B0aW9ucywgb3B0aW9ucyk7XG4gICAgICByZXR1cm4gdGhpcztcbiAgICB9LFxuICAgIHN0YXJ0OiBmdW5jdGlvbiAoKSB7XG4gICAgICBfaW50cm9Gb3JFbGVtZW50LmNhbGwodGhpcywgdGhpcy5fdGFyZ2V0RWxlbWVudCk7XG4gICAgICByZXR1cm4gdGhpcztcbiAgICB9LFxuICAgIGdvVG9TdGVwOiBmdW5jdGlvbihzdGVwKSB7XG4gICAgICBfZ29Ub1N0ZXAuY2FsbCh0aGlzLCBzdGVwKTtcbiAgICAgIHJldHVybiB0aGlzO1xuICAgIH0sXG4gICAgbmV4dFN0ZXA6IGZ1bmN0aW9uKCkge1xuICAgICAgX25leHRTdGVwLmNhbGwodGhpcyk7XG4gICAgICByZXR1cm4gdGhpcztcbiAgICB9LFxuICAgIHByZXZpb3VzU3RlcDogZnVuY3Rpb24oKSB7XG4gICAgICBfcHJldmlvdXNTdGVwLmNhbGwodGhpcyk7XG4gICAgICByZXR1cm4gdGhpcztcbiAgICB9LFxuICAgIGV4aXQ6IGZ1bmN0aW9uKCkge1xuICAgICAgX2V4aXRJbnRyby5jYWxsKHRoaXMsIHRoaXMuX3RhcmdldEVsZW1lbnQpO1xuICAgIH0sXG4gICAgcmVmcmVzaDogZnVuY3Rpb24oKSB7XG4gICAgICBfc2V0SGVscGVyTGF5ZXJQb3NpdGlvbi5jYWxsKHRoaXMsIGRvY3VtZW50LnF1ZXJ5U2VsZWN0b3IoJy5pbnRyb2pzLWhlbHBlckxheWVyJykpO1xuICAgICAgcmV0dXJuIHRoaXM7XG4gICAgfSxcbiAgICBvbmJlZm9yZWNoYW5nZTogZnVuY3Rpb24ocHJvdmlkZWRDYWxsYmFjaykge1xuICAgICAgaWYgKHR5cGVvZiAocHJvdmlkZWRDYWxsYmFjaykgPT09ICdmdW5jdGlvbicpIHtcbiAgICAgICAgdGhpcy5faW50cm9CZWZvcmVDaGFuZ2VDYWxsYmFjayA9IHByb3ZpZGVkQ2FsbGJhY2s7XG4gICAgICB9IGVsc2Uge1xuICAgICAgICB0aHJvdyBuZXcgRXJyb3IoJ1Byb3ZpZGVkIGNhbGxiYWNrIGZvciBvbmJlZm9yZWNoYW5nZSB3YXMgbm90IGEgZnVuY3Rpb24nKTtcbiAgICAgIH1cbiAgICAgIHJldHVybiB0aGlzO1xuICAgIH0sXG4gICAgb25jaGFuZ2U6IGZ1bmN0aW9uKHByb3ZpZGVkQ2FsbGJhY2spIHtcbiAgICAgIGlmICh0eXBlb2YgKHByb3ZpZGVkQ2FsbGJhY2spID09PSAnZnVuY3Rpb24nKSB7XG4gICAgICAgIHRoaXMuX2ludHJvQ2hhbmdlQ2FsbGJhY2sgPSBwcm92aWRlZENhbGxiYWNrO1xuICAgICAgfSBlbHNlIHtcbiAgICAgICAgdGhyb3cgbmV3IEVycm9yKCdQcm92aWRlZCBjYWxsYmFjayBmb3Igb25jaGFuZ2Ugd2FzIG5vdCBhIGZ1bmN0aW9uLicpO1xuICAgICAgfVxuICAgICAgcmV0dXJuIHRoaXM7XG4gICAgfSxcbiAgICBvbmFmdGVyY2hhbmdlOiBmdW5jdGlvbihwcm92aWRlZENhbGxiYWNrKSB7XG4gICAgICBpZiAodHlwZW9mIChwcm92aWRlZENhbGxiYWNrKSA9PT0gJ2Z1bmN0aW9uJykge1xuICAgICAgICB0aGlzLl9pbnRyb0FmdGVyQ2hhbmdlQ2FsbGJhY2sgPSBwcm92aWRlZENhbGxiYWNrO1xuICAgICAgfSBlbHNlIHtcbiAgICAgICAgdGhyb3cgbmV3IEVycm9yKCdQcm92aWRlZCBjYWxsYmFjayBmb3Igb25hZnRlcmNoYW5nZSB3YXMgbm90IGEgZnVuY3Rpb24nKTtcbiAgICAgIH1cbiAgICAgIHJldHVybiB0aGlzO1xuICAgIH0sXG4gICAgb25jb21wbGV0ZTogZnVuY3Rpb24ocHJvdmlkZWRDYWxsYmFjaykge1xuICAgICAgaWYgKHR5cGVvZiAocHJvdmlkZWRDYWxsYmFjaykgPT09ICdmdW5jdGlvbicpIHtcbiAgICAgICAgdGhpcy5faW50cm9Db21wbGV0ZUNhbGxiYWNrID0gcHJvdmlkZWRDYWxsYmFjaztcbiAgICAgIH0gZWxzZSB7XG4gICAgICAgIHRocm93IG5ldyBFcnJvcignUHJvdmlkZWQgY2FsbGJhY2sgZm9yIG9uY29tcGxldGUgd2FzIG5vdCBhIGZ1bmN0aW9uLicpO1xuICAgICAgfVxuICAgICAgcmV0dXJuIHRoaXM7XG4gICAgfSxcbiAgICBvbmV4aXQ6IGZ1bmN0aW9uKHByb3ZpZGVkQ2FsbGJhY2spIHtcbiAgICAgIGlmICh0eXBlb2YgKHByb3ZpZGVkQ2FsbGJhY2spID09PSAnZnVuY3Rpb24nKSB7XG4gICAgICAgIHRoaXMuX2ludHJvRXhpdENhbGxiYWNrID0gcHJvdmlkZWRDYWxsYmFjaztcbiAgICAgIH0gZWxzZSB7XG4gICAgICAgIHRocm93IG5ldyBFcnJvcignUHJvdmlkZWQgY2FsbGJhY2sgZm9yIG9uZXhpdCB3YXMgbm90IGEgZnVuY3Rpb24uJyk7XG4gICAgICB9XG4gICAgICByZXR1cm4gdGhpcztcbiAgICB9XG4gIH07XG5cbiAgZXhwb3J0cy5pbnRyb0pzID0gaW50cm9KcztcbiAgcmV0dXJuIGludHJvSnM7XG59KSk7XG5cblxuXG4vLy8vLy8vLy8vLy8vLy8vLy9cbi8vIFdFQlBBQ0sgRk9PVEVSXG4vLyAuL34vaW50cm8uanMvaW50cm8uanNcbi8vIG1vZHVsZSBpZCA9IC4vbm9kZV9tb2R1bGVzL2ludHJvLmpzL2ludHJvLmpzXG4vLyBtb2R1bGUgY2h1bmtzID0gMiAxNSAxNiIsIi8qKiogSU1QT1JUUyBGUk9NIGltcG9ydHMtbG9hZGVyICoqKi9cbnZhciAkID0gcmVxdWlyZShcImpxdWVyeVwiKTtcbnZhciBqUXVlcnkgPSByZXF1aXJlKFwianF1ZXJ5XCIpO1xuKGZ1bmN0aW9uKCkge1xuXG4vKiFcbiAqIGpRdWVyeSBDb29raWUgUGx1Z2luIHYxLjQuMVxuICogaHR0cHM6Ly9naXRodWIuY29tL2NhcmhhcnRsL2pxdWVyeS1jb29raWVcbiAqXG4gKiBDb3B5cmlnaHQgMjAxMyBLbGF1cyBIYXJ0bFxuICogUmVsZWFzZWQgdW5kZXIgdGhlIE1JVCBsaWNlbnNlXG4gKi9cbihmdW5jdGlvbiAoZmFjdG9yeSkge1xuXHRpZiAodHlwZW9mIGRlZmluZSA9PT0gJ2Z1bmN0aW9uJyAmJiBkZWZpbmUuYW1kKSB7XG5cdFx0Ly8gQU1EXG5cdFx0ZGVmaW5lKFsnanF1ZXJ5J10sIGZhY3RvcnkpO1xuXHR9IGVsc2UgaWYgKHR5cGVvZiBleHBvcnRzID09PSAnb2JqZWN0Jykge1xuXHRcdC8vIENvbW1vbkpTXG5cdFx0ZmFjdG9yeShyZXF1aXJlKCdqcXVlcnknKSk7XG5cdH0gZWxzZSB7XG5cdFx0Ly8gQnJvd3NlciBnbG9iYWxzXG5cdFx0ZmFjdG9yeShqUXVlcnkpO1xuXHR9XG59KGZ1bmN0aW9uICgkKSB7XG5cblx0dmFyIHBsdXNlcyA9IC9cXCsvZztcblxuXHRmdW5jdGlvbiBlbmNvZGUocykge1xuXHRcdHJldHVybiBjb25maWcucmF3ID8gcyA6IGVuY29kZVVSSUNvbXBvbmVudChzKTtcblx0fVxuXG5cdGZ1bmN0aW9uIGRlY29kZShzKSB7XG5cdFx0cmV0dXJuIGNvbmZpZy5yYXcgPyBzIDogZGVjb2RlVVJJQ29tcG9uZW50KHMpO1xuXHR9XG5cblx0ZnVuY3Rpb24gc3RyaW5naWZ5Q29va2llVmFsdWUodmFsdWUpIHtcblx0XHRyZXR1cm4gZW5jb2RlKGNvbmZpZy5qc29uID8gSlNPTi5zdHJpbmdpZnkodmFsdWUpIDogU3RyaW5nKHZhbHVlKSk7XG5cdH1cblxuXHRmdW5jdGlvbiBwYXJzZUNvb2tpZVZhbHVlKHMpIHtcblx0XHRpZiAocy5pbmRleE9mKCdcIicpID09PSAwKSB7XG5cdFx0XHQvLyBUaGlzIGlzIGEgcXVvdGVkIGNvb2tpZSBhcyBhY2NvcmRpbmcgdG8gUkZDMjA2OCwgdW5lc2NhcGUuLi5cblx0XHRcdHMgPSBzLnNsaWNlKDEsIC0xKS5yZXBsYWNlKC9cXFxcXCIvZywgJ1wiJykucmVwbGFjZSgvXFxcXFxcXFwvZywgJ1xcXFwnKTtcblx0XHR9XG5cblx0XHR0cnkge1xuXHRcdFx0Ly8gUmVwbGFjZSBzZXJ2ZXItc2lkZSB3cml0dGVuIHBsdXNlcyB3aXRoIHNwYWNlcy5cblx0XHRcdC8vIElmIHdlIGNhbid0IGRlY29kZSB0aGUgY29va2llLCBpZ25vcmUgaXQsIGl0J3MgdW51c2FibGUuXG5cdFx0XHQvLyBJZiB3ZSBjYW4ndCBwYXJzZSB0aGUgY29va2llLCBpZ25vcmUgaXQsIGl0J3MgdW51c2FibGUuXG5cdFx0XHRzID0gZGVjb2RlVVJJQ29tcG9uZW50KHMucmVwbGFjZShwbHVzZXMsICcgJykpO1xuXHRcdFx0cmV0dXJuIGNvbmZpZy5qc29uID8gSlNPTi5wYXJzZShzKSA6IHM7XG5cdFx0fSBjYXRjaChlKSB7fVxuXHR9XG5cblx0ZnVuY3Rpb24gcmVhZChzLCBjb252ZXJ0ZXIpIHtcblx0XHR2YXIgdmFsdWUgPSBjb25maWcucmF3ID8gcyA6IHBhcnNlQ29va2llVmFsdWUocyk7XG5cdFx0cmV0dXJuICQuaXNGdW5jdGlvbihjb252ZXJ0ZXIpID8gY29udmVydGVyKHZhbHVlKSA6IHZhbHVlO1xuXHR9XG5cblx0dmFyIGNvbmZpZyA9ICQuY29va2llID0gZnVuY3Rpb24gKGtleSwgdmFsdWUsIG9wdGlvbnMpIHtcblxuXHRcdC8vIFdyaXRlXG5cblx0XHRpZiAodmFsdWUgIT09IHVuZGVmaW5lZCAmJiAhJC5pc0Z1bmN0aW9uKHZhbHVlKSkge1xuXHRcdFx0b3B0aW9ucyA9ICQuZXh0ZW5kKHt9LCBjb25maWcuZGVmYXVsdHMsIG9wdGlvbnMpO1xuXG5cdFx0XHRpZiAodHlwZW9mIG9wdGlvbnMuZXhwaXJlcyA9PT0gJ251bWJlcicpIHtcblx0XHRcdFx0dmFyIGRheXMgPSBvcHRpb25zLmV4cGlyZXMsIHQgPSBvcHRpb25zLmV4cGlyZXMgPSBuZXcgRGF0ZSgpO1xuXHRcdFx0XHR0LnNldFRpbWUoK3QgKyBkYXlzICogODY0ZSs1KTtcblx0XHRcdH1cblxuXHRcdFx0cmV0dXJuIChkb2N1bWVudC5jb29raWUgPSBbXG5cdFx0XHRcdGVuY29kZShrZXkpLCAnPScsIHN0cmluZ2lmeUNvb2tpZVZhbHVlKHZhbHVlKSxcblx0XHRcdFx0b3B0aW9ucy5leHBpcmVzID8gJzsgZXhwaXJlcz0nICsgb3B0aW9ucy5leHBpcmVzLnRvVVRDU3RyaW5nKCkgOiAnJywgLy8gdXNlIGV4cGlyZXMgYXR0cmlidXRlLCBtYXgtYWdlIGlzIG5vdCBzdXBwb3J0ZWQgYnkgSUVcblx0XHRcdFx0b3B0aW9ucy5wYXRoICAgID8gJzsgcGF0aD0nICsgb3B0aW9ucy5wYXRoIDogJycsXG5cdFx0XHRcdG9wdGlvbnMuZG9tYWluICA/ICc7IGRvbWFpbj0nICsgb3B0aW9ucy5kb21haW4gOiAnJyxcblx0XHRcdFx0b3B0aW9ucy5zZWN1cmUgID8gJzsgc2VjdXJlJyA6ICcnXG5cdFx0XHRdLmpvaW4oJycpKTtcblx0XHR9XG5cblx0XHQvLyBSZWFkXG5cblx0XHR2YXIgcmVzdWx0ID0ga2V5ID8gdW5kZWZpbmVkIDoge307XG5cblx0XHQvLyBUbyBwcmV2ZW50IHRoZSBmb3IgbG9vcCBpbiB0aGUgZmlyc3QgcGxhY2UgYXNzaWduIGFuIGVtcHR5IGFycmF5XG5cdFx0Ly8gaW4gY2FzZSB0aGVyZSBhcmUgbm8gY29va2llcyBhdCBhbGwuIEFsc28gcHJldmVudHMgb2RkIHJlc3VsdCB3aGVuXG5cdFx0Ly8gY2FsbGluZyAkLmNvb2tpZSgpLlxuXHRcdHZhciBjb29raWVzID0gZG9jdW1lbnQuY29va2llID8gZG9jdW1lbnQuY29va2llLnNwbGl0KCc7ICcpIDogW107XG5cblx0XHRmb3IgKHZhciBpID0gMCwgbCA9IGNvb2tpZXMubGVuZ3RoOyBpIDwgbDsgaSsrKSB7XG5cdFx0XHR2YXIgcGFydHMgPSBjb29raWVzW2ldLnNwbGl0KCc9Jyk7XG5cdFx0XHR2YXIgbmFtZSA9IGRlY29kZShwYXJ0cy5zaGlmdCgpKTtcblx0XHRcdHZhciBjb29raWUgPSBwYXJ0cy5qb2luKCc9Jyk7XG5cblx0XHRcdGlmIChrZXkgJiYga2V5ID09PSBuYW1lKSB7XG5cdFx0XHRcdC8vIElmIHNlY29uZCBhcmd1bWVudCAodmFsdWUpIGlzIGEgZnVuY3Rpb24gaXQncyBhIGNvbnZlcnRlci4uLlxuXHRcdFx0XHRyZXN1bHQgPSByZWFkKGNvb2tpZSwgdmFsdWUpO1xuXHRcdFx0XHRicmVhaztcblx0XHRcdH1cblxuXHRcdFx0Ly8gUHJldmVudCBzdG9yaW5nIGEgY29va2llIHRoYXQgd2UgY291bGRuJ3QgZGVjb2RlLlxuXHRcdFx0aWYgKCFrZXkgJiYgKGNvb2tpZSA9IHJlYWQoY29va2llKSkgIT09IHVuZGVmaW5lZCkge1xuXHRcdFx0XHRyZXN1bHRbbmFtZV0gPSBjb29raWU7XG5cdFx0XHR9XG5cdFx0fVxuXG5cdFx0cmV0dXJuIHJlc3VsdDtcblx0fTtcblxuXHRjb25maWcuZGVmYXVsdHMgPSB7fTtcblxuXHQkLnJlbW92ZUNvb2tpZSA9IGZ1bmN0aW9uIChrZXksIG9wdGlvbnMpIHtcblx0XHRpZiAoJC5jb29raWUoa2V5KSA9PT0gdW5kZWZpbmVkKSB7XG5cdFx0XHRyZXR1cm4gZmFsc2U7XG5cdFx0fVxuXG5cdFx0Ly8gTXVzdCBub3QgYWx0ZXIgb3B0aW9ucywgdGh1cyBleHRlbmRpbmcgYSBmcmVzaCBvYmplY3QuLi5cblx0XHQkLmNvb2tpZShrZXksICcnLCAkLmV4dGVuZCh7fSwgb3B0aW9ucywgeyBleHBpcmVzOiAtMSB9KSk7XG5cdFx0cmV0dXJuICEkLmNvb2tpZShrZXkpO1xuXHR9O1xuXG59KSk7XG5cbn0uY2FsbCh3aW5kb3cpKTtcblxuXG4vLy8vLy8vLy8vLy8vLy8vLy9cbi8vIFdFQlBBQ0sgRk9PVEVSXG4vLyAuL34vanF1ZXJ5LmNvb2tpZS9qcXVlcnkuY29va2llLmpzXG4vLyBtb2R1bGUgaWQgPSAuL25vZGVfbW9kdWxlcy9qcXVlcnkuY29va2llL2pxdWVyeS5jb29raWUuanNcbi8vIG1vZHVsZSBjaHVua3MgPSAyIDE1IDE2IiwiaW1wb3J0ICQgZnJvbSAnanF1ZXJ5JztcbmltcG9ydCBJbnRyb0pzIGZyb20gJ2ludHJvLmpzJztcbmltcG9ydCBfIGZyb20gJ3VuZGVyc2NvcmUnO1xuaW1wb3J0ICdqcXVlcnkuY29va2llJztcblxuZXhwb3J0IGRlZmF1bHQgKGludHJvTmFtZSkgPT4ge1xuXHQvLyBlc2xpbnQtZGlzYWJsZS1uZXh0LWxpbmUgbmV3LWNhcFxuXHRjb25zdCBpbnRybyA9IG5ldyBJbnRyb0pzLmludHJvSnMoKTtcblx0aW50cm8ubmFtZSA9IGludHJvTmFtZTtcblxuXHRpbnRyby5zZXRPcHRpb25zKHtcblx0XHQvKiBOZXh0IGJ1dHRvbiBsYWJlbCBpbiB0b29sdGlwIGJveCAqL1xuXHRcdG5leHRMYWJlbDogJ05leHQgJnJhcnI7Jyxcblx0XHQvKiBQcmV2aW91cyBidXR0b24gbGFiZWwgaW4gdG9vbHRpcCBib3ggKi9cblx0XHRwcmV2TGFiZWw6ICcmbGFycjsgQmFjaycsXG5cdFx0LyogU2tpcCBidXR0b24gbGFiZWwgaW4gdG9vbHRpcCBib3ggKi9cblx0XHRza2lwTGFiZWw6ICdSZWFkIExhdGVyJyxcblx0XHQvKiBEb25lIGJ1dHRvbiBsYWJlbCBpbiB0b29sdGlwIGJveCAqL1xuXHRcdGRvbmVMYWJlbDogJ0ZpbmlzaCcsXG5cdFx0LyogRGVmYXVsdCB0b29sdGlwIGJveCBwb3NpdGlvbiAqL1xuXHRcdHRvb2x0aXBQb3NpdGlvbjogJ2JvdHRvbScsXG5cdFx0LyogTmV4dCBDU1MgY2xhc3MgZm9yIHRvb2x0aXAgYm94ZXMgKi9cblx0XHR0b29sdGlwQ2xhc3M6ICd3bS1pbnRyb2pzJyxcblx0XHQvKiBDbG9zZSBpbnRyb2R1Y3Rpb24gd2hlbiBwcmVzc2luZyBFc2NhcGUgYnV0dG9uPyAqL1xuXHRcdGV4aXRPbkVzYzogdHJ1ZSxcblx0XHQvKiBDbG9zZSBpbnRyb2R1Y3Rpb24gd2hlbiBjbGlja2luZyBvbiBvdmVybGF5IGxheWVyPyAqL1xuXHRcdGV4aXRPbk92ZXJsYXlDbGljazogdHJ1ZSxcblx0XHQvKiBTaG93IHN0ZXAgbnVtYmVycyBpbiBpbnRyb2R1Y3Rpb24/ICovXG5cdFx0c2hvd1N0ZXBOdW1iZXJzOiBmYWxzZSxcblx0XHQvKiBMZXQgdXNlciB1c2Uga2V5Ym9hcmQgdG8gbmF2aWdhdGUgdGhlIHRvdXI/ICovXG5cdFx0a2V5Ym9hcmROYXZpZ2F0aW9uOiB0cnVlLFxuXHRcdC8qIFNob3cgdG91ciBjb250cm9sIGJ1dHRvbnM/ICovXG5cdFx0c2hvd0J1dHRvbnM6IHRydWUsXG5cdFx0LyogU2hvdyB0b3VyIGJ1bGxldHM/ICovXG5cdFx0c2hvd0J1bGxldHM6IHRydWUsXG5cdFx0LyogU2Nyb2xsIHRvIGhpZ2hsaWdodGVkIGVsZW1lbnQ/ICovXG5cdFx0c2Nyb2xsVG9FbGVtZW50OiB0cnVlLFxuXHRcdC8qIFNldCB0aGUgb3ZlcmxheSBvcGFjaXR5ICovXG5cdFx0b3ZlcmxheU9wYWNpdHk6IDAuOFxuXHR9KTtcblxuXHQvLyBXaGVuIHVzZXIgY29tcGxldGVzIGEgam95cmlkZSwgd2Ugc2V0IGEgY29va2llIGFuZCBwb3N0IHRoZSB2aXNpdCB0byB0aGVcblx0Ly8gdmlzaXRlZF9yZXNvdXJjZXMgdGFibGVcblx0Y29uc3QgX29uY29tcGxldGUgPSBpbnRyby5vbmNvbXBsZXRlO1xuXHRpbnRyby5vbmNvbXBsZXRlID0gXy5jb21wb3NlKF9vbmNvbXBsZXRlLCAoZnVuYykgPT4ge1xuXHRcdHJldHVybiBmdW5jdGlvbiAoKSB7XG5cdFx0XHQvLyBSZWNvcmQgdmlzaXQgdG8gdGhpcyByZXNvdXJjZVxuXHRcdFx0JC5hamF4KHtcblx0XHRcdFx0dXJsOiBgL3RyYWNraW5nL21lcmdlP3Jlc291cmNlTmFtZT0ke2ludHJvLm5hbWV9YFxuXHRcdFx0fSk7XG5cblx0XHRcdC8vIFNldCBjb29raWUgdG8gYXZvaWQgdW5uZWNlc3NhcnkgYWpheCByZXF1ZXN0XG5cdFx0XHQkLmNvb2tpZShpbnRyby5uYW1lLCAndmlld2VkJywgeyBleHBpcmVzOiAzNjUsIHBhdGg6ICcvJyB9KTtcblxuXHRcdFx0Ly8gR29vZ2xlIGFuYWx5dGljc1xuXHRcdFx0Z2EoJ3NlbmQnLCAncGFnZXZpZXcnLCBgL2ludHJvLyR7aW50cm8ubmFtZX0vZmluaXNoYCk7XG5cblx0XHRcdGZ1bmMoKTtcblx0XHR9O1xuXHR9KTtcblx0aW50cm8ub25jb21wbGV0ZShfLm5vb3ApO1xuXG5cdC8vIFNlbmQgdmlydHVhbCBwYWdldmlldyBmb3IgZWFjaCBzdGVwIG9mIHRoZSBqb3lyaWRlIHRoYXQgaXMgaGl0XG5cdGNvbnN0IF9vbmFmdGVyY2hhZ2UgPSBpbnRyby5vbmFmdGVyY2hhbmdlO1xuXHRpbnRyby5vbmFmdGVyY2hhbmdlID0gXy5jb21wb3NlKF9vbmFmdGVyY2hhZ2UsIChmdW5jKSA9PiB7XG5cdFx0cmV0dXJuIGZ1bmN0aW9uIChlKSB7XG5cdFx0XHQvLyBHb29nbGUgYW5hbHl0aWNzXG5cdFx0XHRnYSgnc2VuZCcsICdwYWdldmlldycsIGAvaW50cm8vJHtpbnRyby5uYW1lfS8ke2ludHJvLl9jdXJyZW50U3RlcH1gKTtcblx0XHRcdGZ1bmMoZSk7XG5cdFx0fTtcblx0fSk7XG5cdGludHJvLm9uYWZ0ZXJjaGFuZ2UoXy5ub29wKTtcblxuXHQvLyBXYXRjaCBvbmNlIGVuc3VyZXMgdGhlIGpveXJpZGUgZG9lcyBub3QgcnVuIGlmIHRoZSB1c2VyIGhhcyBhbHJlYWR5IHNlZW4gaXRcblx0aW50cm8ud2F0Y2hPbmNlID0gZnVuY3Rpb24gKCkge1xuXHRcdC8vIENoZWNrIGZvciBjb29raWUgZmlyc3QuLi4gdGhhdCdzIGZhc3QgYW5kIGNoZWFwXG5cdFx0aWYgKCQuY29va2llKGludHJvLm5hbWUpKSB7XG5cdFx0XHQvLyBVc2VyIGhhcyBhbHJlYWR5IHZpZXdlZCB0aGlzIGpveXJpZGVcblx0XHRcdHJldHVybjtcblx0XHR9XG5cblx0XHQvLyBSZXF1ZXN0IGxpc3Qgb2Ygdmlld2VkIGpveXJpZGVzIGZyb20gc2VydmVyIG5leHQuLi5cblx0XHQkLmFqYXgoe1xuXHRcdFx0dXJsOiAnL3RyYWNraW5nJyxcblx0XHRcdHN1Y2Nlc3MgKHJlc3BvbnNlKSB7XG5cdFx0XHRcdC8vIEZpbmQgaW50cm8ubmFtZSBpbiB0aGUgdmlzaXRlZCByZXNvdXJjZXMgbGlzdD9cblx0XHRcdFx0aWYgKF8uY29udGFpbnMocmVzcG9uc2UudmlzaXRlZExpc3QsIGludHJvLm5hbWUpKSB7XG5cdFx0XHRcdFx0Ly8gVXNlciBoYXMgYWxyZWFkeSB2aWV3ZWQgdGhpc1xuXHRcdFx0XHRcdHJldHVybjtcblx0XHRcdFx0fVxuXG5cdFx0XHRcdC8vIFVzZXIgZG9lcyBub3QgYXBwZWFyIHRvIGhhdmUgdmlld2VkIHRoaXMuLi4gc2hvdyBpdCFcblx0XHRcdFx0aW50cm8uc3RhcnQoKTtcblx0XHRcdH1cblx0XHR9KTtcblx0fTtcblxuXHRyZXR1cm4gaW50cm87XG59O1xuXG5cblxuXG4vLyBXRUJQQUNLIEZPT1RFUiAvL1xuLy8gLi9zcmMvbWFpbi93ZWJhcHAvbWVkaWEvc2NyaXB0cy93ZWJwYWNrL2NvbmZpZy9pbnRyb2pzLmpzIl0sInNvdXJjZVJvb3QiOiIifQ==