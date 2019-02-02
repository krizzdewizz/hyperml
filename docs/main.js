let main;

(function (main) {

    const ELEMENT_NODE = 1;
    const TEXT_NODE = 3;

    function acceptChildren(node, visitor) {
        node.childNodes.forEach(child => accept(child, visitor));
    }

    function accept(node, visitor) {
        visitor.start(node);
        acceptChildren(node, visitor);
        visitor.end(node);
    }

    function noChildElements(node) {
        let result = true;
        node.childNodes.forEach(child => {
            if (child.nodeType === ELEMENT_NODE) {
                result = false;
            }
        });
        return result;
    }

    function stringValue(s) {
        const num = Number(s);
        if (!Number.isNaN(num)) {
            return num;
        }
        s = s
            .replace(/\n/g, '\\n')
            .replace(/"/g, '\\"');
        return `"${s}"`;
    }

    function textOnlyChild(node) {
        const children = node.childNodes;
        if (children.length === 1 && children[0].nodeType === TEXT_NODE) {
            return children[0].textContent.trim();
        }
        return undefined;
    }

    function nodeName(node) {
        const name = node.nodeName.toLowerCase();
        return name.startsWith('xx') ? name.substring(2) : name;
    }

    function Visitor() {
        this.output = '';

        let depth = 0;

        const indent = () => '    '.repeat(depth);
        const print = s => this.output += `${indent()}${s}`;

        let skipNext = false;

        this.start = node => {
            if (skipNext) {
                return;
            }
            if (node.nodeType === ELEMENT_NODE) {
                const name = nodeName(node);

                const pairs = [];
                for (let i = 0, n = node.attributes.length; i < n; i++) {
                    const a = node.attributes[i];
                    const knownAttr = KNOWN_ATTRIBUTES[a.name];
                    const attrName = knownAttr ? knownAttr : `"${a.name}"`;
                    const attrValue = a.value ? stringValue(a.value) : true;
                    pairs.push(attrName, attrValue);
                }

                const pairsString = pairs.join(', ');

                const textOnly = textOnlyChild(node);
                if (textOnly) {
                    print(`${name}(${pairsString ? pairsString + ', ' : ''}${stringValue(textOnly)}, $);\n`);
                    skipNext = true;
                }

                if (!skipNext) {
                    const voidEl = VOID_ELEMENTS[name];
                    let end = '';
                    if (!voidEl && noChildElements(node)) {
                        end = pairsString ? ', $' : '$';
                        skipNext = true;
                    }
                    print(`${name}(${pairsString}${end});\n`);
                    if (voidEl || end) {
                        skipNext = true;
                    } else {
                        print(`{\n`);
                    }
                }
                depth++;

            } else if (node.nodeType === TEXT_NODE) {
                const text = node.textContent.trim();
                if (text) {
                    print(`text(${stringValue(text)});\n`);
                }
            }
        };

        this.end = node => {
            if (node.nodeType === ELEMENT_NODE) {
                depth--;
                if (skipNext) {
                    skipNext = false;
                    return;
                }
                print(`}\n${indent()}$();\n`);
            }
        };
    }

    main.generate = () => {
        const htmlEl = document.querySelector('#html');
        const outEl = document.querySelector('#output');

        const el = document.createElement('div');
        const html = htmlEl.value
            .replace(/<html/g, '<xxhtml')
            .replace(/<\/html/g, '</xxhtml');

        el.innerHTML = html;

        const visitor = new Visitor();
        acceptChildren(el, visitor);

        outEl.textContent = visitor.output;
    };

    var KNOWN_ATTRIBUTES = {
        '_blank': '_blank',
        '_parent': '_parent',
        '_self': '_self',
        '_top': '_top',
        'abbr': 'abbr',
        'above': 'above',
        'accept': 'accept',
        'accept-charset': 'acceptCharset',
        'accesskey': 'accesskey',
        'action': 'action',
        'align': 'align',
        'all': 'all',
        'allow-forms': 'allowForms',
        'allow-same-origin': 'allowSameOrigin',
        'allow-scripts': 'allowScripts',
        'allow-top-navigation': 'allowTopNavigation',
        'alt': 'alt',
        'any': 'any',
        'application/x-www-form-urlencoded': 'applicationXWwwFormUrlencoded',
        'archive': 'archive',
        'async': 'async',
        'auto': 'auto',
        'autocomplete': 'autocomplete',
        'autofocus': 'autofocus',
        'autoplay': 'autoplay',
        'axis': 'axis',
        'azimuth': 'azimuth',
        'background': 'background',
        'background-attachment': 'backgroundAttachment',
        'background-color': 'backgroundColor',
        'background-image': 'backgroundImage',
        'background-position': 'backgroundPosition',
        'background-repeat': 'backgroundRepeat',
        'baseline': 'baseline',
        'below': 'below',
        'border': 'border',
        'border-bottom': 'borderBottom',
        'border-bottom-color': 'borderBottomColor',
        'border-bottom-style': 'borderBottomStyle',
        'border-bottom-width': 'borderBottomWidth',
        'border-collapse': 'borderCollapse',
        'border-color': 'borderColor',
        'border-left': 'borderLeft',
        'border-left-color': 'borderLeftColor',
        'border-left-style': 'borderLeftStyle',
        'border-left-width': 'borderLeftWidth',
        'border-right': 'borderRight',
        'border-right-color': 'borderRightColor',
        'border-right-style': 'borderRightStyle',
        'border-right-width': 'borderRightWidth',
        'border-spacing': 'borderSpacing',
        'border-style': 'borderStyle',
        'border-top': 'borderTop',
        'border-top-color': 'borderTopColor',
        'border-top-style': 'borderTopStyle',
        'border-top-width': 'borderTopWidth',
        'border-width': 'borderWidth',
        'bottom': 'bottom',
        'box': 'box',
        'box-sizing': 'boxSizing',
        'button': 'button',
        'caption-side': 'captionSide',
        'captions': 'captions',
        'caret-color': 'caretColor',
        'cellpadding': 'cellpadding',
        'cellspacing': 'cellspacing',
        'center': 'center',
        'ch': 'ch',
        'chains': 'chains',
        'challenge': 'challenge',
        'chapters': 'chapters',
        'char': 'charr',
        'charoff': 'charoff',
        'charset': 'charset',
        'checkbox': 'checkbox',
        'checked': 'checked',
        'circle': 'circle',
        'cite': 'cite',
        'class': 'classs',
        'classid': 'classid',
        'clear': 'clear',
        'clip': 'clip',
        'cm': 'cm',
        'codebase': 'codebase',
        'codetype': 'codetype',
        'col': 'col',
        'colgroup': 'colgroup',
        'color': 'color',
        'cols': 'cols',
        'colspan': 'colspan',
        'command': 'command',
        'content': 'content',
        'content-language': 'contentLanguage',
        'content-type': 'contentType',
        'contenteditable': 'contenteditable',
        'context': 'context',
        'contextmenu': 'contextmenu',
        'controls': 'controls',
        'coords': 'coords',
        'copy': 'copy',
        'counter-increment': 'counterIncrement',
        'counter-reset': 'counterReset',
        'cue': 'cue',
        'cue-after': 'cueAfter',
        'cue-before': 'cueBefore',
        'cursor': 'cursor',
        'data': 'data',
        'date': 'date',
        'datetime': 'datetime',
        'datetime-local': 'datetimeLocal',
        'declare': 'declare',
        'default': 'defaultt',
        'default-style': 'defaultStyle',
        'defer': 'defer',
        'descriptions': 'descriptions',
        'dir': 'dir',
        'direction': 'direction',
        'dirname': 'dirname',
        'disabled': 'disabled',
        'display': 'display',
        'draggable': 'draggable',
        'dropzone': 'dropzone',
        'elevation': 'elevation',
        'em': 'em',
        'email': 'email',
        'empty-cells': 'emptyCells',
        'enctype': 'enctype',
        'ex': 'ex',
        'false': 'falsee',
        'file': 'file',
        'float': 'floatt',
        'flow': 'flow',
        'font': 'font',
        'font-family': 'fontFamily',
        'font-feature-settings': 'fontFeatureSettings',
        'font-kerning': 'fontKerning',
        'font-size': 'fontSize',
        'font-size-adjust': 'fontSizeAdjust',
        'font-stretch': 'fontStretch',
        'font-style': 'fontStyle',
        'font-synthesis': 'fontSynthesis',
        'font-variant': 'fontVariant',
        'font-variant-caps': 'fontVariantCaps',
        'font-variant-east-asian': 'fontVariantEastAsian',
        'font-variant-ligatures': 'fontVariantLigatures',
        'font-variant-numeric': 'fontVariantNumeric',
        'font-variant-position': 'fontVariantPosition',
        'font-weight': 'fontWeight',
        'for': 'forr',
        'form': 'form',
        'formaction': 'formaction',
        'formenctype': 'formenctype',
        'formmethod': 'formmethod',
        'formnovalidate': 'formnovalidate',
        'formtarget': 'formtarget',
        'frame': 'frame',
        'get': 'get',
        'grid': 'grid',
        'grid-template': 'gridTemplate',
        'grid-template-areas': 'gridTemplateAreas',
        'grid-template-columns': 'gridTemplateColumns',
        'grid-template-rows': 'gridTemplateRows',
        'groups': 'groups',
        'hard': 'hard',
        'headers': 'headers',
        'height': 'height',
        'hidden': 'hidden',
        'high': 'high',
        'href': 'href',
        'hreflang': 'hreflang',
        'hsides': 'hsides',
        'http-equiv': 'httpEquiv',
        'icon': 'icon',
        'id': 'id',
        'image': 'image',
        'in': 'in',
        'ismap': 'ismap',
        'justify': 'justify',
        'keytype': 'keytype',
        'kind': 'kind',
        'label': 'label',
        'lang': 'lang',
        'left': 'left',
        'letter-spacing': 'letterSpacing',
        'lhs': 'lhs',
        'line-height': 'lineHeight',
        'link': 'link',
        'list': 'list',
        'list-style': 'listStyle',
        'list-style-image': 'listStyleImage',
        'list-style-position': 'listStylePosition',
        'list-style-type': 'listStyleType',
        'longdesc': 'longdesc',
        'loop': 'loop',
        'low': 'low',
        'ltr': 'ltr',
        'manifest': 'manifest',
        'margin': 'margin',
        'margin-bottom': 'marginBottom',
        'margin-left': 'marginLeft',
        'margin-right': 'marginRight',
        'margin-top': 'marginTop',
        'max': 'max',
        'max-height': 'maxHeight',
        'max-width': 'maxWidth',
        'maxlength': 'maxlength',
        'media': 'media',
        'mediagroup': 'mediagroup',
        'metadata': 'metadata',
        'method': 'method',
        'middle': 'middle',
        'min': 'min',
        'min-height': 'minHeight',
        'min-width': 'minWidth',
        'mm': 'mm',
        'month': 'month',
        'move': 'move',
        'multipart/form-data': 'multipartFormData',
        'multiple': 'multiple',
        'muted': 'muted',
        'name': 'name',
        'nohref': 'nohref',
        'none': 'none',
        'novalidate': 'novalidate',
        'number': 'number',
        'object': 'object',
        'off': 'off',
        'on': 'on',
        'onabort': 'onabort',
        'onafterprint': 'onafterprint',
        'onbeforeprint': 'onbeforeprint',
        'onbeforeunload': 'onbeforeunload',
        'onblur': 'onblur',
        'oncanplay': 'oncanplay',
        'oncanplaythrough': 'oncanplaythrough',
        'onchange': 'onchange',
        'onclick': 'onclick',
        'oncontextmenu': 'oncontextmenu',
        'ondblclick': 'ondblclick',
        'ondrag': 'ondrag',
        'ondragend': 'ondragend',
        'ondragenter': 'ondragenter',
        'ondragleave': 'ondragleave',
        'ondragover': 'ondragover',
        'ondragstart': 'ondragstart',
        'ondrop': 'ondrop',
        'ondurationchange': 'ondurationchange',
        'onemptied': 'onemptied',
        'onended': 'onended',
        'onerror': 'onerror',
        'onfocus': 'onfocus',
        'onhashchange': 'onhashchange',
        'oninput': 'oninput',
        'oninvalid': 'oninvalid',
        'onkeydown': 'onkeydown',
        'onkeypress': 'onkeypress',
        'onkeyup': 'onkeyup',
        'onload': 'onload',
        'onloadeddata': 'onloadeddata',
        'onloadedmetadata': 'onloadedmetadata',
        'onloadstart': 'onloadstart',
        'onmessage': 'onmessage',
        'onmousedown': 'onmousedown',
        'onmousemove': 'onmousemove',
        'onmouseout': 'onmouseout',
        'onmouseover': 'onmouseover',
        'onmouseup': 'onmouseup',
        'onmousewheel': 'onmousewheel',
        'onoffline': 'onoffline',
        'ononline': 'ononline',
        'onpause': 'onpause',
        'onplay': 'onplay',
        'onplaying': 'onplaying',
        'onpopstate': 'onpopstate',
        'onprogress': 'onprogress',
        'onratechange': 'onratechange',
        'onreadystatechange': 'onreadystatechange',
        'onredo': 'onredo',
        'onreset': 'onreset',
        'onresize': 'onresize',
        'onscroll': 'onscroll',
        'onseeked': 'onseeked',
        'onseeking': 'onseeking',
        'onselect': 'onselect',
        'onshow': 'onshow',
        'onstalled': 'onstalled',
        'onstorage': 'onstorage',
        'onsubmit': 'onsubmit',
        'onsuspend': 'onsuspend',
        'ontimeupdate': 'ontimeupdate',
        'onundo': 'onundo',
        'onunload': 'onunload',
        'onvolumechange': 'onvolumechange',
        'onwaiting': 'onwaiting',
        'opacity': 'opacity',
        'open': 'open',
        'optimum': 'optimum',
        'orphans': 'orphans',
        'outline': 'outline',
        'outline-color': 'outlineColor',
        'outline-offset': 'outlineOffset',
        'outline-style': 'outlineStyle',
        'outline-width': 'outlineWidth',
        'overflow': 'overflow',
        'padding': 'padding',
        'padding-bottom': 'paddingBottom',
        'padding-left': 'paddingLeft',
        'padding-right': 'paddingRight',
        'padding-top': 'paddingTop',
        'page-break-after': 'pageBreakAfter',
        'page-break-before': 'pageBreakBefore',
        'page-break-inside': 'pageBreakInside',
        'password': 'password',
        'pattern': 'pattern',
        'pause': 'pause',
        'pause-after': 'pauseAfter',
        'pause-before': 'pauseBefore',
        'pc': 'pc',
        '%': 'percent',
        'pitch': 'pitch',
        'pitch-range': 'pitchRange',
        'placeholder': 'placeholder',
        'play-during': 'playDuring',
        'poly': 'poly',
        'position': 'position',
        'post': 'post',
        'poster': 'poster',
        'preload': 'preload',
        'presentation-level': 'presentationLevel',
        'profile': 'profile',
        'pt': 'pt',
        'pubdate': 'pubdate',
        'px': 'px',
        'quotes': 'quotes',
        'radio': 'radio',
        'radiogroup': 'radiogroup',
        'range': 'range',
        'readonly': 'readonly',
        'rect': 'rect',
        'ref': 'ref',
        'refresh': 'refresh',
        'rel': 'rel',
        'rem': 'rem',
        'required': 'required',
        'reset': 'reset',
        'resize': 'resize',
        'rest': 'rest',
        'rest-after': 'restAfter',
        'rest-before': 'restBefore',
        'rev': 'rev',
        'reversed': 'reversed',
        'rhs': 'rhs',
        'richness': 'richness',
        'right': 'right',
        'row': 'row',
        'rowgroup': 'rowgroup',
        'rows': 'rows',
        'rowspan': 'rowspan',
        'rsa': 'rsa',
        'rtl': 'rtl',
        'rules': 'rules',
        'sandbox': 'sandbox',
        'scheme': 'scheme',
        'scope': 'scope',
        'scoped': 'scoped',
        'seamless': 'seamless',
        'search': 'search',
        'selected': 'selected',
        'set-cookie': 'setCookie',
        'shape': 'shape',
        'size': 'size',
        'sizes': 'sizes',
        'soft': 'soft',
        'span': 'span',
        'speak': 'speak',
        'speak-as': 'speakAs',
        'speak-header': 'speakHeader',
        'speak-numeral': 'speakNumeral',
        'speak-punctuation': 'speakPunctuation',
        'speech-rate': 'speechRate',
        'spellcheck': 'spellcheck',
        'src': 'src',
        'srcdoc': 'srcdoc',
        'srclang': 'srclang',
        'standby': 'standby',
        'start': 'start',
        'step': 'step',
        'stress': 'stress',
        'style': 'style',
        'submit': 'submit',
        'subtitles': 'subtitles',
        'summary': 'summary',
        'tabindex': 'tabindex',
        'table-layout': 'tableLayout',
        'target': 'target',
        'tel': 'tel',
        'text': 'text',
        'text-align': 'textAlign',
        'text-decoration': 'textDecoration',
        'text-indent': 'textIndent',
        'text-overflow': 'textOverflow',
        'text-transform': 'textTransform',
        'text/plain': 'textPlain',
        'time': 'time',
        'title': 'title',
        'toolbar': 'toolbar',
        'top': 'top',
        'true': 'truee',
        'type': 'type',
        'unicode-bidi': 'unicodeBidi',
        'url': 'url',
        'usemap': 'usemap',
        'valign': 'valign',
        'value': 'value',
        'valuetype': 'valuetype',
        'vertical-align': 'verticalAlign',
        'vh': 'vh',
        'visibility': 'visibility',
        'vmax': 'vmax',
        'vmin': 'vmin',
        'voice-balance': 'voiceBalance',
        'voice-duration': 'voiceDuration',
        'voice-family': 'voiceFamily',
        'voice-pitch': 'voicePitch',
        'voice-range': 'voiceRange',
        'voice-rate': 'voiceRate',
        'voice-stress': 'voiceStress',
        'voice-volume': 'voiceVolume',
        'void': 'voidd',
        'volume': 'volume',
        'vsides': 'vsides',
        'vwv': 'vwv',
        'week': 'week',
        'white-space': 'whiteSpace',
        'widows': 'widows',
        'width': 'width',
        'word-spacing': 'wordSpacing',
        'wrap': 'wrap',
        'z-index': 'zIndex'
    };

    var VOID_ELEMENTS = { 'area': 1, 'base': 1, 'br': 1, 'col': 1, 'embed': 1, 'hr': 1, 'img': 1, 'input': 1, 'keygen': 1, 'link': 1, 'meta': 1, 'param': 1, 'source': 1, 'track': 1, 'wbr': 1 };
})(main || (main = {}));