var domain = "tbecker2.dyndns.org";
var client_id = "821447254243.apps.googleusercontent.com";
var redirect_url = "http://" + domain + "/oauthcallback";
var scope = "https://www.googleapis.com/auth/buzz";
var auth_token_endpoint = 'https://accounts.google.com/o/oauth2/auth?response_type=token&client_id=' + client_id + '&redirect_uri=' + redirect_url + '&scope=' + scope;

$.get(auth_token_endpoint, function(data) {
	alert('Load was performed.' + data);
});

(function($) {

	var cometd = $.cometd;

	$(document).ready(
			function() {
				function _connectionEstablished() {
					$('#body').append(
							'<div>CometD Connection Established</div>');
				}

				function _connectionBroken() {
					$('#body').append('<div>CometD Connection Broken</div>');
				}

				function _connectionClosed() {
					$('#body').append('<div>CometD Connection Closed</div>');
				}

				// Function that manages the connection status with the Bayeux
				// server
				var _connected = false;
				function _metaConnect(message) {
					if (cometd.isDisconnected()) {
						_connected = false;
						_connectionClosed();
						return;
					}

					var wasConnected = _connected;
					_connected = message.successful === true;
					if (!wasConnected && _connected) {
						_connectionEstablished();
					} else if (wasConnected && !_connected) {
						_connectionBroken();
					}
				}

				// Function invoked when first contacting the server and
				// when the server has lost the state of this client
				function _metaHandshake(handshake) {
					if (handshake.successful === true) {
						cometd.batch(function() {
							cometd.subscribe('/hello', function(message) {
								$('#body').append(
										'<div>Server Says: '
												+ message.data.greeting
												+ '</div>');
							});
							// Publish on a service channel since the message is
							// for the server only
							cometd.publish('/service/hello', {
								name : 'World'
							});
						});
					}
				}

				// Disconnect when the page unloads
				$(window).unload(function() {
					cometd.disconnect(true);
				});

				var cometURL = location.protocol + "//" + location.host
						+ config.contextPath + "/cometd";
				cometd.configure({
					url : cometURL,
					logLevel : 'debug'
				});

				cometd.addListener('/meta/handshake', _metaHandshake);
				cometd.addListener('/meta/connect', _metaConnect);

				cometd.handshake();
			});
})(jQuery);
