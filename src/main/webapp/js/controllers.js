var app = angular.module('thales', ['ngRoute', 'angles']);

app.config(['$routeProvider',
	function($routeProvider) {
		$routeProvider.
		when('/about', {
			templateUrl: 'views/about.html',
			controller: 'aboutCtrl'
		}).
		when('/optimum', {
			templateUrl: 'views/optimum.html',
			controller: 'optimumCtrl'
		}).
		when('/testbench', {
			templateUrl: 'views/testbench.html',
			controller: 'testbenchCtrl'
		}).
		when('/library', {
			templateUrl: 'views/library.html',
			controller: 'libraryCtrl'
		}).
		when('/toolbox', {
			templateUrl: 'views/toolbox.html',
			controller: 'toolboxCtrl'
		}).
		when('/toolbox/cellmodels', {
			templateUrl: 'views/toolbox-cellmodels.html',
			controller: 'toolbox-cellmodelsCtrl'
		}).
		when('/toolbox/history', {
			templateUrl: 'views/toolbox-history.html',
			controller: 'toolbox-historyCtrl'
		}).
		when('/toolbox/self', {
			templateUrl: 'views/toolbox-self.html',
			controller: 'toolbox-selfCtrl'
		}).
		otherwise({
			redirectTo: '/about'
		});
	}
]);

/*

Application Settings

- Nombre de chiffres significatifs lors de l'affichage des nombres
*/

app.settings = {
		numberPrecision: 3
};

/*

Main Controller

*/

app.controller('mainCtrl', ['$scope',
	function($scope) {
	
	// Charger les scripts (matjax) dynamiquement
	$scope.loadScript = function(url, type, charset) {
	    if (type===undefined) type = 'text/javascript';
	    if (url) {
	        var script = document.querySelector("script[src*='"+url+"']");
	        if (!script) {
	            var heads = document.getElementsByTagName("head");
	            if (heads && heads.length) {
	                var head = heads[0];
	                if (head) {
	                    script = document.createElement('script');
	                    script.setAttribute('src', url);
	                    script.setAttribute('type', type);
	                    if (charset) script.setAttribute('charset', charset);
	                    head.appendChild(script);
	                }
	            }
	        }
	        return script;
	    }
	};
	}
]);

/*

About Controller

*/

app.controller('aboutCtrl', ['$scope',
	function($scope) {
	}]);

/*

Toolbox Controller

*/

app.controller('toolboxCtrl', function($scope) {
	
});

/*

Optimum Controller

*/

app.controller('optimumCtrl', function($scope, $http, $filter) {
	// Init
	$scope.display = function(val) {
		return new Big(val).toPrecision(app.settings.numberPrecision);
	}; // cette fonction peut etre reprise de Testbench
	$scope.is_backend_ready = {};
	$scope.transistors = {};
	$scope.diodes = {};
	$scope.sim = {};
	$scope.sim.MAX_ITERATIONS = 8;
	$scope.resultsToShow = 30;
	$scope.displayOutput = false;
	$scope.successMessage = "";
	$scope.results = [];
	$scope.response = [];
	$scope.parameters = [
	               	  {
	               		  'name': 'Vin',
	               		  'shortName': 'Vin',
	               		  'unit': 'V'
	               	  },
	               	  {
	               		  'name': 'Vout',
	               		  'shortName': 'Vout',
	               		  'unit': 'V'
	               	  },
	               	  {
	               		  'name': 'Fréquence',
	               		  'shortName': 'F',
	               		  'unit': 'Hz'
	               	  },
	               	  {
	               		  'name': 'Iout',
	               		  'shortName': 'Iout',
	               		  'unit': 'A'
	               	  }
	               	];
	$http.get('apis/circuits').
	success(function(data, status, headers, config) {
		$scope.circuits = data;
		$scope.is_backend_ready.c = true;
	}).
	error(function(data, status, headers, config) {
		console.log("Erreur dans la requête http.get('apis/circuits')");
	});
	$http.get('apis/transistors').
	success(function(data, status, headers, config) {
		$scope.is_backend_ready.t = true;
		for(var t in data) {
			$scope.transistors[t] = data[t];
			$scope.transistors[t]['selected'] = true;
		}
	}).
	error(function(data, status, headers, config) {
		console.log("Erreur dans la requête http.get('apis/transistors')");
	});
	$http.get('apis/diodes').
	success(function(data, status, headers, config) {
		$scope.diodes = data;
		$scope.is_backend_ready.d = true;

		for(var d in data) {
			$scope.diodes[d] = data[d];
			$scope.diodes[d]['selected'] = true;
		}
	}).
	error(function(data, status, headers, config) {
		console.log("Erreur dans la requête http.get('apis/diodes')");
	});
	
	// Variables. setVariables est appelee quand on selectionne un circuit
	$scope.setVariables = function() {
    	$http.post('variables', { "circuit": $scope.selectedCircuit}).
  		success(function(data, status, headers, config) {
  			$scope.variables = angular.fromJson(data);
  			for(var v in $scope.variables) {
  				k = $scope.variables[v];
  				$scope.sim[k.shortName] = "" + k.value; // Le "" sert a caster k.value (Double) en String
  			}
  		}).
  		error(function(data, status, headers, config) {
    		alert('Variables : Erreur dans la requête $http.get');
  		});
    };	
    
    // Simulation
    $scope.execute = function() {
    	// base reprend les valeurs de base de la simulation, celles qui ne concernent pas le transistor ou la diode. On la declare ici pour la fixer dans le scope.
    	var base = $scope.sim;
    	var list = [];
		$scope.displayOutput = true;
    	// D'abord, on compte le nombre de calculs a effectuer
    	var i = 0;
    	for(var t in $scope.transistors) {
    		if($scope.transistors[t].selected) {
    			for(var d in $scope.diodes) {
    	    		if($scope.diodes[d].selected) {
    	    			$scope.results.push("# " + $scope.transistors[t].Name + ' : \t' + $scope.diodes[d].name);
    	    			list.push({
    	    				't': $scope.transistors[t],
    	    				'd': $scope.diodes[d]
    	    			});
    	    			i++;
    	    		}
    			}
    		}
    	}
    	$scope.successMessage = "0 / " + i + " tests effectués.";
    	
    	// Ensuite, on calcule
    	results = [];
    	request = [];
    	for(var k=0; k < list.length; k++) {
    		(function(j) {	
    			// On declare de nouvelles 
    			var t = {};
    			var s = {};
    			var d = {};
   		
    			// On reprend les valeurs du transistor et de la diode
    			t = list[j].t;
    			d = list[j].d;
    			
    			// On inclut les valeurs des objets simulation,  transistor et diode dans l'objet de simulation.
    			for(a in base) {
        			s[a] = base[a];
    			}
    			for(a in t) {
        			s[a] = t[a];
    			}
    			s['transistorName'] = t['Name'];
    		
    			for(a in d) {
    				s[a] = d[a];	
    			}
    			s['diodeName'] = d['name'];	
    			
    			s['index'] = j;
    			
    			// Et on lance la requete ajax
    			dataInput = {};
            	dataInput.form = s;
            	dataInput.circuit = $scope.selectedCircuit;
            	dataInput.max_iterations = $scope.sim['MAX_ITERATIONS'] + "";
            	$http({
            		method: 'POST',
            		url: 'compute',
            		data: dataInput,
            		headers: {
            			'Content-Type': 'application/json'
            		}
            	}).
            	success(function(data, status, headers, config) {
            		output = data;
            		s['PertesTotales'] = $filter('filter')(output, {shortName: "PertesTotales"})[0]['value'];
            	}).
            	error(function(data, status, headers, config) {
            		alert('Erreur dans la requête $http.post');
            	});
            	

    			request.push(s);

    		})(k);	
    		$scope.successMessage = " tests effectués.";
    	}

    	results = request;
    	
    	$scope.response = results;
    	console.log($scope.response);
    	
    };
    
    
    $scope.printResults = function () {
    	$scope.response = results;
    };
});

/*

Toolbox - Modeles Controller

*/

app.controller('toolbox-cellmodelsCtrl', function($scope, $http) {
	// Init
	tests = {};
	tree = {};
	cells = [];
	$scope.coefficients = {};
	$scope.displayCell = false;
	$scope.plotu = {
			'a': 60,
			'b': 100,
			'c': 150
	};
	$scope.plotf = 200000;
	$scope.$parent.loadScript('https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML', 'text/javascript', 'utf-8');

	// List tests
	$http.get('apis/tests').
	success(function(data, status, headers, config) {
		tests = data;
		// On va parcourir les tests et ajouter les cellules progressivement.
		for(k in tests) {
			t = tests[k];
			console.log(t.dutycycle);
			if(!tree[t.type]) {
				tree[t.type] = {};
			}
			
			if(!tree[t.type][t.component1]) {
				tree[t.type][t.component1] = {};
			}
			
			if(!tree[t.type][t.component1][t.component2]) {
				tree[t.type][t.component1][t.component2] = {};
			}
			
			if(!tree[t.type][t.component1][t.component2][t.dutycycle]) {
				tree[t.type][t.component1][t.component2][t.dutycycle] = {};
				console.log("easy " + [t.dutycycle]);
				// La cellule n'existe pas encore, on l'ajoute
				cells[t.type + "-" + t.component1 + "-" + t.component2 + "-" + t.dutycycle] = {
					type: t.type,
					component1: t.component1,
					component2: t.component2,
					dutycycle: t.dutycycle,
					tests: []
				};
			}	
			
			cells[t.type + "-" + t.component1 + "-" + t.component2 + "-" + t.dutycycle].tests.push({
				id: t.id,
				type: t.type,
				component1: t.component1,
				component2: t.component2,
				voltage: t.voltage,
				current: t.current,
				losses: t.losses,
				dutycycle: t.dutycycle,
				frequency: t.frequency,
				date: t.date,
				searchString: t.type + "-" + t.component1 + "-" + t.component2 + "-" + t.dutycycle
			});
		
		}
		
		$scope.cells = [];
		// comme cells est un objet et non un array
		for (var property in cells) {
			if (cells.hasOwnProperty(property)) {
				$scope.cells.push(cells[property]);
			}
		}
	
	}).
	error(function(data, status, headers, config) {
		console.log("Tests : Erreur dans la requête http.get('tests')");
	});

	$scope.model = function(index) {
		$scope.cell = $scope.cells[index];
		tests = $scope.cells[index].tests;
       	$http.post('cellmodels', tests).
		success(function(data, status, headers, config) {
			for(var i in data) {
				$scope.coefficients[i] = new Big(data[i]).toPrecision(app.settings.numberPrecision);	
			}
			$scope.displayCell = true;
		}).
		error(function(data, status, headers, config) {
			alert("Modele : Erreur dans la requête http.post('cellmodel')");
		});
		
		
	};
	
	$scope.plot = function() {
		plot();
	};
		// Input - getting coefficients
		$scope.is_backend_ready = true;
		
		// Treatment - computing values
		var pertes = {};
		pertes.conduction = {};
		pertes.commutation = {};
		pertes.totales = {};

		function Pcond(u,i,f) {
			return $scope.coefficients[0]*i + $scope.coefficients[1]*i*i;
		}

		function Pcom(u,i,f) {
			 return f*($scope.coefficients[2]*u*i + $scope.coefficients[3]*u*u + $scope.coefficients[4]*i*i);
		}
		
		function plot() {
			pertes.conduction.y = new Array();
			pertes.conduction.y2 = new Array();
			pertes.conduction.y3 = new Array();
			pertes.conduction.x = new Array();
			pertes.commutation.y = new Array();
			pertes.commutation.y2 = new Array();
			pertes.commutation.y3 = new Array();
			pertes.commutation.x = new Array();
			pertes.totales.y = new Array();
			pertes.totales.y2 = new Array();
			pertes.totales.y3 = new Array();
			pertes.totales.xt = new Array();
			pertes.totales.yt = new Array();
			pertes.totales.yt2 = new Array();
			pertes.totales.yt3 = new Array();
			u = $scope.plotu;
			f = $scope.plotf;
			
			var i = 0;
			var step = 1;
			while(i<15) {
				pertes.conduction.x.push(i);
				pertes.conduction.y.push(Pcond(u.a,i,f));
				pertes.conduction.y2.push(Pcond(u.b,i,f));
				pertes.conduction.y3.push(Pcond(u.c,i,f));
	
				pertes.commutation.x.push(i);
				pertes.commutation.y.push(Pcom(u.a,i,f));
				pertes.commutation.y2.push(Pcom(u.b,i,f));
				pertes.commutation.y3.push(Pcom(u.c,i,f));

				pertes.totales.y.push(Pcond(u.a,i,f) + Pcom(u.a,i,f));
				pertes.totales.y2.push(Pcond(u.b,i,f) + Pcom(u.b,i,f));
				pertes.totales.y3.push(Pcond(u.c,i,f) + Pcom(u.c,i,f));
				pertes.totales.yt.push(null);
				pertes.totales.yt2.push(null);
				pertes.totales.yt3.push(null);
				
				i = i + step;
			}
			
			tests = $scope.cell.tests;
			for(k in tests) {
				if(tests[k].voltage == u.a && tests[k].frequency == f) {
					pertes.totales.yt[Math.round(tests[k].current)] = tests[k].losses;
				}
				if(tests[k].voltage == u.b && tests[k].frequency == f) {
					pertes.totales.yt2[Math.round(tests[k].current)] = tests[k].losses;
				}
				if(tests[k].voltage == u.c && tests[k].frequency == f) {
					pertes.totales.yt3[Math.round(tests[k].current)] = tests[k].losses;
				}
			}
			console.log(pertes.totales.yt2);
			
	
			// Output
			$scope.chart = {
	  	  		labels : pertes.conduction.x,
	  		  	datasets : [
	        	{
	        		label: "W",
	            	fillColor : "rgba(151,187,205,0.01)",
	            	strokeColor : "rgb(75, 137, 184)",
	            	data : pertes.conduction.y
	        	},
	        	{
	        		label: "W",
	            	fillColor : "rgba(151,187,205,0.01)",
	            	strokeColor : "rgb(45, 137, 184)",
	            	data : pertes.conduction.y2
	        	},
	        	{
	        		label: "W",
	            	fillColor : "rgba(151,187,205,0.01)",
	            	strokeColor : "rgb(145, 45, 184)",
	            	data : pertes.conduction.y3
	        	}
	    		],
			};

			$scope.chart2 = {
	  	  		labels : pertes.commutation.x,
	  		  	datasets : [
	        	{	
	        		label: "W",
	            	fillColor : "rgba(151,187,205,0.01)",
	            	strokeColor : "rgb(75, 137, 184)",
	            	data : pertes.commutation.y
	        	},
	        	{	
	        		label: "W",
	            	fillColor : "rgba(51,87,205,0.01)",
	            	strokeColor : "rgb(45, 207, 134)",
	            	data : pertes.commutation.y2
	        	},
	        	{	
	        		label: "W",
	            	fillColor : "rgba(51,87,205,0.01)",
	            	strokeColor : "rgb(145, 45, 184)",
	            	data : pertes.commutation.y3
	        	}
	    		],
			};
			
			$scope.chart3 = {
		  	  		labels : pertes.commutation.x,
		  		  	datasets : [
		        	{	
		        		label: "W",
		            	fillColor : "rgba(151,187,205,0.01)",
		            	strokeColor : "rgb(75, 137, 184)",
		            	data : pertes.totales.y
		        	},
		        	{	
		        		label: "W",
		            	fillColor : "rgba(51,87,205,0.01)",
		            	strokeColor : "rgb(45, 207, 134)",
		            	data : pertes.totales.y2
		        	},
		        	{	
		        		label: "W",
		            	fillColor : "rgba(51,87,205,0.01)",
		            	strokeColor : "rgb(145, 45, 184)",
		            	data : pertes.totales.y3
		        	},
		        	{	
		        		label: "W",
		            	fillColor : "rgba(10,10,50,0.05)",
		            	strokeColor : "rgb(0, 0, 0)",
		            	pointDotRadius : 50,
		            	data : pertes.totales.yt
		        	},
		        	{	
		        		label: "W",
		            	fillColor : "rgba(10,10,50,0.05)",
		            	strokeColor : "rgb(0, 0, 0)",
		            	pointDotRadius : 50,
		            	data : pertes.totales.yt2
		        	},
		        	{	
		        		label: "W",
		            	fillColor : "rgba(10,10,50,0.05)",
		            	strokeColor : "rgb(0, 0, 0)",
		            	pointDotRadius : 50,
		            	data : pertes.totales.yt3
		        	}
		    		],
				};
			$scope.chart.options = {responsive: false,  pointDotRadius : 2, pointHitDetectionRadius : 20, showScale: true, scaleShowLabels: true, scaleBeginAtZero: true};
		}
	}
);

/*

Test bench Controller

*/

app.controller('testbenchCtrl', ['$scope', '$http', '$window', '$filter', function($scope, $http, $window, $filter) {

	// Init
	$scope.sim = {};
	$scope.circuits = {};
	$scope.variables = {};
	$scope.diodes = {};
	$scope.transistors = {};
	$scope.displayOutput = false;
	$scope.displayFailure = false;
	$scope.displayOutputLoader = false;
	$scope.sim.options = {};
	$scope.results = {};
	$scope.failureMessage = "";
	$scope.is_circuit_selected = false;
	$(document).ready(function () {
		$('[data-toggle="tooltip"]').tooltip();
	});
	
	$scope.display = function(val) {
		return new Big(val).toPrecision(app.settings.numberPrecision);
	};
	
	// Variables. setVariables est appelee quand on selectionne un circuit
	$scope.setVariables = function() {
    	$http.post('variables', { "circuit": $scope.selectedCircuit}).
  		success(function(data, status, headers, config) {
  			$scope.variables = angular.fromJson(data);
  			for(var v in $scope.variables) {
  				k = $scope.variables[v];
  				$scope.sim[k.shortName] = "" + k.value; // Le "" sert a caster k.value (Double) en String
  			}
  			
  			// On met aussi l'image du circuit
  			$scope.sim.circuitName = $filter('filter')($scope.circuits, {id: $scope.selectedCircuit})[0]['shortName'];
  			console.log($scope.sim.circuitName);
  		}).
  		error(function(data, status, headers, config) {
    		alert('Variables : Erreur dans la requête $http.get');
  		});
    };	
    
	// Circuits & Components
	// List
	$http.get('apis/circuits').
	success(function(data, status, headers, config) {
		$scope.circuits = data;
		$scope.is_backend_ready_c = true; 
	}).
	error(function(data, status, headers, config) {
		console.log("Circuit : Erreur dans la requête http.get('apis/circuits')");
	});
	
	$http.get('apis/transistors').
	success(function(data, status, headers, config) {
		$scope.transistors = data;
		$scope.is_backend_ready = true;  
	}).
	error(function(data, status, headers, config) {
		console.log("Transistors : Erreur dans la requête http.get('apis/transistors')");
	});

	$http.get('apis/diodes').
	success(function(data, status, headers, config) {
		$scope.diodes = data;
		$scope.is_backend_ready_d = true;  
	}).
	error(function(data, status, headers, config) {
		console.log("Diodes : Erreur dans la requête http.get('apis/diodes')");
	});	

	// Quand on selectionne un transistor
	$scope.is_transistor_selected = false;
	$("#selectTransistor").change(function() {
		j = $("#selectTransistor option:selected").val();
		$scope.sim.t = {};
		for (var k in $scope.transistors[j]) {
			n = k.charAt(0).toUpperCase() + k.slice(1);
			$scope.sim[n] = $scope.transistors[j][k];
			$scope.sim.t[n] = $scope.transistors[j][k];
   		}
		$scope.is_transistor_selected = true;
		$scope.$apply();
	});

	// Quand on selectionne une diode
	$scope.is_diode_selected = false;
	$("#selectDiode").change(function() {
		j = $("#selectDiode option:selected").val();
		$scope.sim.d = {};
		for (var k in $scope.diodes[j]) {
			n = k.charAt(0).toUpperCase() + k.slice(1);
			$scope.sim[k] = $scope.diodes[j][k];
			$scope.sim.d[k] = $scope.diodes[j][k];
   		}
		$scope.is_diode_selected = true;
		$scope.$apply();
	});

	// Simulation
    $scope.compute = function() {
    	dataInput = {};
    	dataInput.form = $scope.sim;
    	dataInput.circuit = $scope.selectedCircuit;
    	dataInput.max_iterations = 10 + "";
    	console.log("Starting computation.. \nInput : ");
    	console.log($scope.sim);
    
    	$http({
    		method: 'POST',
    		url: 'compute',
    		data: dataInput,
    		headers: {
    			'Content-Type': 'application/json'
    		}
    	}).
    	success(function(data, status, headers, config) {
    		tree = {};
    		for(var i in data) {
    			v = data[i];
    			if(!tree[v.group]) {
    				tree[v.group] = [];
    			}
    			tree[v.group].push(data[i]);
    		}
    		$scope.results = {
    			output: tree,
    			input: $scope.sim
    		};

    		// On check si la simulation n'a pas produit d'erreurs et affiche la sortie
    		if(tree.Hidden[0].expression === "true") { // si success = true
        		$scope.displayOutput = true;
        		$scope.displayFailure = false;
        		$scope.sim.options.showOnlyTotal = true;
        		enableSaveTest();
    		}
    		else {
        		$scope.displayOutput = false;
    			$scope.displayFailure = true;
    			$scope.failureMessage = tree.Hidden[1].expression;
    		}
    		
    		$scope.displayOutputLoader = false;
    	}).
    	error(function(data, status, headers, config) {
    		alert('Erreur dans la requête $http.post');
    	});
    };
    
    $scope.setTooltips = function() {
    	$("[data-toggle='tooltip']").tooltip();
    };
    
    // Enregistrer test
    $scope.saveTest = function() {
    	losses = $filter('filter')($scope.results.output.Total, {name: "Pertes totales"})[0].value;
    	console.log($scope.selectedCircuit);
    	// calcul 
    	test = {
        	"type": $scope.selectedCircuit,
        	"component1": $scope.results.input.t.Name,
        	"component2": $scope.results.input.d.name,
        	"voltage":  $scope.results.input.Vin,
        	"current": $filter('filter')($scope.results.output["Général"], {shortName: "Iout"})[0].value,
        	"dutycycle": $filter('filter')($scope.results.output["Général"], {shortName: "d"})[0].value,
        	"losses": losses,
        	"frequency": $scope.results.input.F,
        	"date": $filter('date')(new Date(), 'yyyy-MM-dd HH:mm:ss'),
    	};
       	$http.post('apis/tests', test).
		success(function(data, status, headers, config) {
			disableSaveTest();
		}).
		error(function(data, status, headers, config) {
			alert("Test : Erreur dans la requête http.post('apis/tests')");
		});
    };
    
    // Bouton enregistrer test 
    function enableSaveTest() {
		$("span#btnSaveTest").addClass("btn-primary");
		$("span#btnSaveTest").addClass("btn");
		$("span#btnSaveTest").removeClass("t-btn-line-height");
		$("span#btnSaveTest").attr("ng-click", "saveTest();");
		$("span#btnSaveTest").text("Enregistrer ce test*");
    }
    
    function disableSaveTest() {
		$("span#btnSaveTest").removeClass("btn-primary");
		$("span#btnSaveTest").removeClass("btn");
		$("span#btnSaveTest").addClass("t-btn-line-height");
		$("span#btnSaveTest").removeAttr("ng-click");
		$("span#btnSaveTest").text("Test enregistré");
    }
    
}]);

/*

Library Controller

*/

app.controller('libraryCtrl', function($scope, $http, $window, $route, $timeout, $filter) {
	// Init
	$scope.transistors = {};
	$scope.diodes = {};
	$scope.circuits = {};
	$scope.transistorMessage = "";
	$scope.diodeMessage = "";
	
	// List, refresh
	$http.get('apis/transistors').
		success(function(data, status, headers, config) {
			$scope.transistors = data;
			$scope.is_backend_ready_t = true;
		}).
		error(function(data, status, headers, config) {
			console.log("Erreur dans la requête http.get('apis/transistors')");
		});
	
	$http.get('apis/diodes').
		success(function(data, status, headers, config) {
			$scope.diodes = data;
			$scope.is_backend_ready_d = true;
	}).
	error(function(data, status, headers, config) {
		console.log("Erreur dans la requête http.get('apis/diodes')");
	});
	
	$http.get('apis/circuits').
	success(function(data, status, headers, config) {
		$scope.circuits = data;
		$scope.is_backend_ready_c = true;
	}).
	error(function(data, status, headers, config) {
		console.log("Erreur dans la requête http.get('apis/circuits')");
	});
   
    
	// Add
    $scope.insertTransistor = function() {
       	$scope.transistorMessage = "Modèle ajouté. Les modifications seront effectives sous peu.";
		
       	$http.post('apis/transistors', $scope.t).
		success(function(data, status, headers, config) {
			$scope.transistors = data;
		}).
		error(function(data, status, headers, config) {
			console.log("Erreur dans la requête http.post('apis/transistors')");
		});
    };
    
    $scope.insertDiode = function() {
       	$scope.diodeMessage = "Modèle ajouté. Les modifications seront effectives sous peu.";
		
       	$http.post('apis/diodes', $scope.d).
		success(function(data, status, headers, config) {
			$scope.diodes = data;
		}).
		error(function(data, status, headers, config) {
			console.log("Erreur dans la requête http.post('apis/diodes')");
		});
    };
    
    $scope.insertCircuit = function() {
       	$http.post('apis/circuits', $scope.c).
		success(function(data, status, headers, config) {
			$scope.circuits = data;
		}).
		error(function(data, status, headers, config) {
			console.log("Erreur dans la requête http.post('apis/circuits')");
		});
    };
    
    // Update
    $scope.updateTransistor = function() {
		$scope.transistorMessage = "Modèle mis à jour. Les modifications seront effectives sous peu.";        
    	
		$http.put('apis/transistors', $scope.t).
		success(function(data, status, headers, config) {
			$scope.transistors = data;
		}).
		error(function(data, status, headers, config) {
			console.log("Erreur dans la requête http.put('apis/transistors')");
		});
    };
    $scope.updateDiode = function() {
		$scope.diodeMessage = "Modèle mis à jour. Les modifications seront effectives sous peu.";        
    	
		$http.put('apis/diodes', $scope.d).
		success(function(data, status, headers, config) {
			$scope.diodes = data;
		}).
		error(function(data, status, headers, config) {
			console.log("Erreur dans la requête http.put('apis/diodes')");
		});
    };
    $scope.updateCircuit = function() {
		$http.put('apis/circuits', $scope.c).
		success(function(data, status, headers, config) {
			$scope.circuits = data;
		}).
		error(function(data, status, headers, config) {
			console.log("Erreur dans la requête http.put('apis/circuits')");
		});
    };

    // Delete
    $scope.deleteTransistor = function() {
        $scope.transistorMessage = "Modèle supprimé. Les modifications seront effectives sous peu.";
        var req = {
        	method: 'DELETE',
        	url: 'apis/transistors',
        	data: $scope.t
        };
        $http(req).
		success(function(data, status, headers, config) {
			$scope.transistors = data;
		}).
		error(function(data, status, headers, config) {
			console.log("Erreur dans la requête http.delete('apis/transistors')");
		});
    };
    
    $scope.deleteDiode = function() {
        $scope.diodeMessage = "Modèle supprimé. Les modifications seront effectives sous peu.";
        var req = {
        	method: 'DELETE',
        	url: 'apis/diodes',
        	data: $scope.d
        };
        $http(req).
		success(function(data, status, headers, config) {
			$scope.diodes = data;
		}).
		error(function(data, status, headers, config) {
			console.log("Erreur dans la requête http.delete('apis/diodes')");
		});
    };
    
    $scope.deleteCircuit = function() {
        var req = {
        	method: 'DELETE',
        	url: 'apis/circuits',
        	data: $scope.c
        };
        $http(req).
		success(function(data, status, headers, config) {
			$scope.circuits = data;
		}).
		error(function(data, status, headers, config) {
			console.log("Erreur dans la requête http.delete('apis/circuits')");
		});
    };
    
    // Remplissage des formulaires d'ajout / modification / suppresion
    $scope.makeUpdateTransistorForm = function(id) {
    	$scope.showUpdateBtn = true;
    	$scope.showInsertBtn = false;
    	$scope.t = $filter('filter')($scope.transistors, {id: id})[0];
    };

    $scope.makeInsertTransistorForm = function() {
    	$scope.showUpdateBtn = false;
    	$scope.showInsertBtn = true;
    	// Il serait bon d'ecrire ceci dynamiquement
    	$scope.t = {"Name": "", "Vgsth": "", "Gm": "", "Cgs": "", "Cgd": "", "Cds": "", "ROn": "", "Ls": ""};
    };
    
    $scope.makeUpdateDiodeForm = function(id) {
    	$scope.showUpdateBtn = true;
    	$scope.showInsertBtn = false;
    	$scope.d = $filter('filter')($scope.diodes, {id: id})[0];
    };

    $scope.makeInsertDiodeForm = function() {
    	$scope.showUpdateBtn = false;
    	$scope.showInsertBtn = true;
    	// Il serait bon d'ecrire ceci dynamiquement
    	$scope.d = {"name": "", "Vd": "", "Cd": ""};
   };
   
   $scope.makeUpdateCircuitForm = function(id) {
   	$scope.showUpdateBtn = true;
   	$scope.showInsertBtn = false;
   	$scope.c = $filter('filter')($scope.circuits, {id: id})[0];
   };

   $scope.makeInsertCircuitForm = function() {
   	$scope.showUpdateBtn = false;
   	$scope.showInsertBtn = true;
   	// Il serait bon d'ecrire ceci dynamiquement
   	$scope.c = {"name": "", "shortName": "", "variables": ""};
  };
});

/*

History Controller

*/

app.controller('toolbox-historyCtrl', function($scope, $http, $filter) {
		$scope.tests = {};
		
		// List
		$http.get('apis/tests').
  		success(function(data, status, headers, config) {
  			$scope.tests = data;
  			for(var i in $scope.tests) {
    			$scope.tests[i].searchString = data[i].type + "-" + data[i].component1 + "-" + data[i].component2;
    		}
  		}).
  		error(function(data, status, headers, config) {
    		console.log("Tests : Erreur dans la requête http.get('tests')");
  		});
		
		// Delete
	    $scope.deleteTest = function() {
	        var req = {
	        	method: 'DELETE',
	        	url: 'apis/tests',
	        	data: $scope.t
	        };
	        $http(req).
			success(function(data, status, headers, config) {
				$scope.tests = data;
				$scope.message = "Test supprimé";
			}).
			error(function(data, status, headers, config) {
				console.log("Erreur dans la requête http.delete('apis/tests')");
			});
	    };
	    
	    // Formulaire de suppression (modals)
	    $scope.makeDeleteForm = function(id) {
	    	console.log($scope.t);
	    	$scope.t = $filter('filter')($scope.tests, {id: id})[0];
	    };
	}
);