var app = angular.module('FTApp', ['LocalStorageModule']);

app.config(['localStorageServiceProvider', function(localStorageServiceProvider) {
	localStorageServiceProvider.setPrefix('FTApp');
}]);

Array.prototype.remove = function() {
    var what, a = arguments, L = a.length, ax;
    while (L && this.length) {
        what = a[--L];
        while ((ax = this.indexOf(what)) !== -1) {
            this.splice(ax, 1);
        }
    }
    return this;
};