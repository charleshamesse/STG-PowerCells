var app = angular.module('thales-login', []);

app.controller('loginCtrl', function($scope, $http) {
	$scope.connected;
	$scope.user = {
			"username": null,
			"password": null
	};
	attempted = false;
	
	$("[data-toggle='tooltip']").tooltip();
	
	// Get status
	$http.get('login').
		success(function(data, status, headers, config) {
			$scope.connected = data;
			console.log($scope.connected);
			go();

		}).
		error(function(data, status, headers, config) {
			console.log("Erreur dans la requête http.get('login')");
		});
	
    $scope.refresh = function() {
    	$route.reload();
    }
    
	// Log in attempt
    $scope.login = function() {
       	$http.post('login', $scope.user).
		success(function(data, status, headers, config) {
			$scope.connected = data;
			console.log($scope.connected);

			attempted = true;
			console.log("attempted: " + attempted);
			if(!$scope.connected) {
				$scope.message = "Identifiants incorrects."
			}
			console.log("message: " + $scope.message);
			
			go();
		}).
		error(function(data, status, headers, config) {
			console.log("Erreur dans la requête http.get('login')");
		});
    }
    
    function go() {
    	if($scope.connected) {
    	setTimeout(function(){
				window.location.reload();
			}, 1000);
		}
    }
    
});
