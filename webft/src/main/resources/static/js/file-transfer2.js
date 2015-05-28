app.controller("FileTransferCtrl", ['$scope', '$interval', '$timeout', '$log', 'localStorageService', function($scope, $interval, $timeout, $log, localStorageService) {
	$scope.title = "WebSocket File Transfer"
	$scope.version = "0.01";
	$scope.$log = $log;
	
	$scope.uploadSlot = 4;
	$scope.uploadMeta = [];
	
	$scope.hashBlockSize = 1024 * 1024;
	$scope.maxBufferedAmount = 64 * 1024;
	
	$scope.chunkSize = 4 * 1024;
	
	$scope.upload = function() {
		var file = document.getElementById('file').files[0];
		$log.log("UPLOAD "+file.name);
		
		$scope.uploadProgress = "Uploading "+file.name+" calculating hash";
		$scope.calculateHash(file, function(hash) {
			$log.info("HASH "+hash);
			$scope.sendData(file, hash);
		});
	};
	
	$scope.sendData = function(file, hash) {
		var ws = new WebSocket("ws://localhost:9191/fileTransfer2");
		ws.onopen = function() {
			var msg = {};
			msg.id = "file";
			msg.file = file.name;
			msg.size = file.size;
			var text = JSON.stringify(msg);
			ws.send(text);
			var hi = 0;
			var block = function() {
				$log.log("BLOCK "+hi+" "+ws.bufferedAmount+" < "+$scope.maxBufferedAmount);
				while (ws.bufferedAmount < $scope.maxBufferedAmount) {
					if (hi < hash.length) {
						var msg = {};
						msg.id = "hash";
						msg.index = hi;
						msg.hash = hash[hi];
						var text = JSON.stringify(msg);
						ws.send(text);
						hi ++;
					} else {
						var msg = {};
						msg.id = "init";
						var text = JSON.stringify(msg);
						ws.send(text);
						return;
					}
				}
				$timeout(block, 1000);
			}
			block();
		};
		ws.onmessage = function(message) {
			var data = JSON.parse(message.data);
			if ("init" == data.id) {
				$log.log("SEND OFFSET "+data.offset);
				$scope.$apply(function() {
					$scope.uploadProgress = "Uploading "+file.name+" "+Math.floor(100*data.offset/file.size)+"%";
				});
				var block = function() {
					$log.log("BLOCK  "+ws.bufferedAmount+" < "+$scope.maxBufferedAmount);
					if (ws.bufferedAmount < $scope.maxBufferedAmount) {
						var r = new FileReader();
						var next = Math.min(data.offset + $scope.chunkSize, file.size);
						var blob = file.slice(data.offset, next);
						r.onload = function(evt) {
							var msg = {};
							msg.id = "data";
							msg.offset = data.offset;
							msg.data = evt.target.result.substring(37);
							var text = JSON.stringify(msg);
							ws.send(text);
							data.offset = next;
							if (data.offset < file.size) {
								block();
							} else {
								var msg = {};
								msg.id = "end";
								var text = JSON.stringify(msg);
								ws.send(text);
							}
						};
						r.readAsDataURL(blob);
					} else {
						$timeout(block, 1000);
					}
				};
				
				block();
			} else if ("data" == data.id) {
				$scope.$apply(function() {
					$scope.uploadProgress = "Uploading "+file.name+" "+Math.floor(100*data.offset/file.size)+"%";
				});
			} else if ("end" == data.id) {
				$scope.$apply(function() {
					$scope.uploadProgress = "Uploading "+file.name+" completed.";
				});
				ws.close();
			} else {
				$log.log("RESP "+message.data);
			}
		}
	    ws.onerror = function (evt) {
			$log.info("Socket error "+JSON.stringify(evt));
	    };
	}
	
	$scope.calculateHash = function(file, callback) {
		var offset = 0;
		var hash = [];
		
		// calculate hash per block
		var onload = function(evt) {
			var data = CryptoJS.enc.Base64.parse(evt.target.result.substring(37));
			var ph = String(CryptoJS.SHA256(data));
			$log.log("DONE READ OFFSET "+offset+"  "+ph);
			hash.push(ph);
			offset += $scope.hashBlockSize;
			if (offset >= file.size) {
				$log.log("DONE HASH "+file.name+" "+hash);
				callback(hash);
			} else {
				block();
			}
		};
		
		var block = function() {
			var r = new FileReader();
			var blob = file.slice(offset, Math.min(offset + $scope.hashBlockSize, file.size));
			r.onload = onload;
			r.readAsDataURL(blob);
		};
		
		block();
	}
	
}]);
