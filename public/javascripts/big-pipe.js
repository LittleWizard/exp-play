(function() {
    "use strict";

    var JSON_CONTENT_TYPE = "json"
    var HTML_CONTENT_TYPE = "html"
    var TEXT_CONTENT_TYPE = "text"

    var root = this;

    var BigPipe = root.BigPipe = {};
    var document = root.document;
    var console = root.console;

    var log = function (message) {
        if (console && console.log) {
            console.log(message);
        }
    };

    BigPipe.unescapeForEmbedding = function (str) {
        if (str) {
            return str.replace(new RegExp('\\\\u002d\\\\u002d', "gi"), '--');
        } else {
            return str;
        }
    };

    BigPipe.readEmbeddedContentFromDom = function (domId) {
        var contentElem = document.getElementById(domId);
        if (contentElem) {
            return BigPipe.unescapeForEmbedding(contentElem.firstChild.nodeValue);
        } else {
            log("ERROR: Unable to read content from DOM with id " + domId + " so return an empty String.");
            return "";
        }
    };

    BigPipe.parseEmbeddedJsonFromDom = function (domId) {
        var content = BigPipe.readEmbeddedContentFromDom(domId);
        return JSON.parse(content);
    };


    BigPipe.renderPageLet = function (id, content) {
        var domElement = document.getElementById(id);
        if (domElement) {
            domElement.innerHTML = content;
        } else {
            log("ERROR: cannot insert pagelet because DOM node with id " + id + " does not exist");
        }
    };


    BigPipe.onPageLet = function (id, contentId, contentType) {
        if (contentType === JSON_CONTENT_TYPE) {
            var json = BigPipe.parseEmbeddedJsonFromDom(contentId);
            BigPipe.renderPageLet(id, json);
        } else if (contentType === HTML_CONTENT_TYPE || contentType === TEXT_CONTENT_TYPE) {
            var content = BigPipe.readEmbeddedContentFromDom(contentId);
            BigPipe.renderPageLet(id, content);
        } else {
            log("ERROR: unsupported contentType " + contentType + " for PagLet with id " + id);
        }
    };

}.call(this));
