(function(window, mustache, BigPipe) {
    "use strict";

    var document = window.document;
    var console = window.console;

    //in real app, you'd probably want to store the template in an external file and
    //not inline like this

    var template =
        '<div class = "module">' +
          '<h3 class = "id">{{ id }} </h3>' +
          '<h6>took</h6>' +
          '<h2 class = "highlight">{{ delay }}</h2> ms' +
          '<h6>to respond</h6>' +
        '</div>';

    //override the original BigPipe.renderPageLet method with one that uses mustache.js for client-side rendering

    BigPipe.renderPageLet = function(id, json) {
        var domElement = document.getElementById(id);
        if(domElement) {
            domElement.innerHTML = Mustache.render(template, json);
        } else {
            console.log("ERROR: cannot render PageLet because DOM node with id " + id + " does not exist");
        }
    };

})(window, Mustache, BigPipe);