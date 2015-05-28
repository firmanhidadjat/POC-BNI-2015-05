app.controller("FileTransferCtrl", ['$scope', '$interval', '$log', 'localStorageService', function($scope, $interval, $log, localStorageService) {
	$scope.title = "WebSocket File Transfer"
	$scope.version = "0.01";
	$scope.$log = $log;
	
	$scope.uploadSlot = 4;
	$scope.uploadMeta = [];
	
	$scope.fs = null;
	$scope.quota = {};
	
	$scope.chunkSize = 4 * 1024;
	
	window.requestFileSystem = window.requestFileSystem || window.webkitRequestFileSystem;

	//Check for support.
	if (window.requestFileSystem) {
		$log.info("FileSystem Supported");
	} else {
		$log.info("FileSystem Not Supported");
	}
	
	navigator.webkitPersistentStorage.queryUsageAndQuota(function(used, remaining) {
		$scope.quota.used = used;
		$scope.quota.remaining = remaining;
		$log.log("Used quota: " + used + ", remaining quota: " + remaining);
	}, $scope.onError);
	
	$scope.upload = function() {
		$scope.file = document.getElementById('file').files[0];
		$log.log("UPLOAD "+JSON.stringify($scope.file));
		
		window.requestFileSystem(navigator.webkitPersistentStorage, $scope.file.size + 1024, function(fs) {
			$scope.fs = fs;
			$scope.writeFile(fs.root, $scope.file);
			
			var meta = {};
			meta.file = $scope.file.name;
			meta.parts = [];
			meta.total = Math.floor(($scope.file.size + $scope.chunkSize - 1) / $scope.chunkSize);
			for (var i=0; i<=meta.total; i++) {
				meta.parts.push(i);
			}
			localStorageService.set("meta."+$scope.file.name, meta);
			$log.info("DONE WRITE SIZE "+$scope.file.size+"  CHUNK "+meta.total);
			
			$scope.doUpload();
		}, $scope.onError);
	};
	
	$scope.writeFile = function(parentDirectory, file) {
		parentDirectory.getFile(file.name, {create:true, exclusive: true}, function(fileEntry) {
			fileEntry.createWriter(function(fileWriter) {
				fileWriter.write(file);
			}, $scope.onError);
		}, $scope.onError);
	};
		
	$scope.onError = function(evt) {
		$log.info("FILE ERROR "+evt.message);
	}
	
	$interval(function() {
		$scope.updateUploadProgress();
	}, 3000);
	
	$scope.ws = new WebSocket("ws://localhost:8080/fileTransfer");
	
	$scope.ws.onopen = function() {
		$log.info("Socket ready");
		$scope.doUpload();
	};
	
	$scope.ws.onmessage = function(message) {
//		$log.info("WS RECEIVE "+message.data);
		var part = JSON.parse(message.data);
		if (part.file != null && part.part != null) {
//			$log.info("WS RECEIVE "+JSON.stringify(part));
	    	for (var i in $scope.uploadMeta) {
	    		var meta = $scope.uploadMeta[i];
	    		if (meta != null) {
	    			if (meta.file == part.file && meta.part == part.part) {
	    				$scope.uploadMeta[i] = null;
	    			}
	    		}
	    	}
	    	var meta = localStorageService.get("meta."+part.file);
	    	meta.parts.remove(part.part);
	    	localStorageService.set("meta."+part.file, meta);
	    	$scope.doUpload();
		}
    };
    
    $scope.ws.onerror = function (evt) {
		$log.info("Socket error "+JSON.stringify(evt));
    }

    $scope.ws.onclose = function (evt) {
		$log.info("Socket closed");
		$scope.ws = null;
    }
    
    $scope.isUploading = function(file, part) {
    	for (var meta of $scope.uploadMeta) {
    		if (meta != null) {
    			if (meta.file == file && meta.part == part) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
	
	$scope.doUpload = function() {
		for (i=$scope.uploadSlot-1; i >= 0; i--) {
			if ($scope.uploadMeta[i] == null) {
				for (var k of localStorageService.keys()) {
					if (k.indexOf("meta.") == 0) {
						var meta = localStorageService.get(k);
						for (var part of meta.parts) {
							if (!$scope.isUploading(meta.file, part)) {
								$scope.uploadMeta[i] = {};
								$scope.uploadMeta[i].file = meta.file;
								$scope.uploadMeta[i].part = part;
//								$log.info("UPLOAD USING SLOT "+i+"  "+meta.file+" "+part);
								if (part == meta.total) {
						    		var msg = {};
						    		msg.file = meta.file;
						    		msg.part = part;
						    		msg.end = true;
									var text = JSON.stringify(msg);
//									$log.info("UPLOAD USING SLOT "+i+"  "+file.name+"  "+part+"  ["+text+"]");
									$scope.ws.send(text);
								} else {
									window.requestFileSystem(TEMPORARY, 0, function(fs) {
										fs.root.getFile(meta.file, {create: false, exclusive: true}, function(fileEntry) {
											fileEntry.file(function(file) {
												var offset = part * $scope.chunkSize;
												var len = Math.min(file.size, offset + $scope.chunkSize);
												var blob = file.slice(offset, len);
										        var r = new FileReader();
										        r.onload = function(evt) {
											        if (evt.target.error == null) {
											    		var msg = {};
											    		msg.file = file.name;
											    		msg.part = part;
											    		msg.data = evt.target.result;
														var text = JSON.stringify(msg);
//														$log.info("UPLOAD USING SLOT "+i+"  "+file.name+"  "+part+"  ["+text+"]");
														$scope.ws.send(text);
											        } else {
											            $log.warn("Read error: " + evt.target.error);
											            return;
											        }
										        };
										        r.readAsDataURL(blob);
											}, $scope.onError);
										}, $scope.onError);
									});
								}
								break;
							}
						}
					}
				}
			}
		}
		$scope.$apply(function() {
			$scope.updateUploadProgress();
		});
	};
	
	$scope.updateUploadProgress = function() {
		var up = "";
		for (var k of localStorageService.keys()) {
			if (k.indexOf("meta.") == 0) {
				var meta = localStorageService.get(k);
				if (meta.parts.length == 0) {
					up = up + meta.file + " completed\n";
				} else if (meta.total > 0) {
					up = up + meta.file + "  " + Math.floor(100 * (meta.total - meta.parts.length) / meta.total) + "%\n";
				} else {
					up = up + meta.file + "  0%\n";
				}
			}
		}
		$scope.uploadProgress = up;
	}
	
	$scope.clearAll = function(file) {
		localStorageService.clearAll();
		window.requestFileSystem(TEMPORARY, 0, function(fs) {
			var dirReader = fs.root.createReader();
			dirReader.readEntries(function(results) {
				for (var r of results) {
					r.remove(function() {
						$log.log("File '"+r.name+"' removed.");
				    }, $scope.onError);
				}
			}, $scope.onError);
		}, $scope.onError);
		$scope.updateUploadProgress();
	}
}]);
