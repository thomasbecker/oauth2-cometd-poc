<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<meta
  http-equiv="Content-Type"
  content="text/html;charset=utf-8" />
<title>SoundCloud OAuth 2 User Agent Authentication Flow Demo</title>
<script
  type="text/javascript"
  src="${pageContext.request.contextPath}/jquery/jquery-1.6.2.js"></script>
<script
  type="text/javascript"
  src="${pageContext.request.contextPath}/jquery/json2.js"></script>
<script
  type="text/javascript"
  src="${pageContext.request.contextPath}/org/cometd.js"></script>
<script
  type="text/javascript"
  src="${pageContext.request.contextPath}/jquery/jquery.cometd.js"></script>
<%--
    The reason to use a JSP is that it is very easy to obtain server-side configuration
    information (such as the contextPath) and pass it to the JavaScript environment on the client.
    --%>
<script
  type="text/javascript"
  charset="utf-8">
	var config = {
		contextPath : '${pageContext.request.contextPath}'
	};

	var cometd = $.cometd;

	$(function() {
		var extractToken = function(hash) {
			var match = hash.match(/access_token=([^&]+)/);
			return !!match && match[1];
		};

		var settings = {
			'clientId' : '821447254243.apps.googleusercontent.com',
			'domain' : 'tbecker2.dyndns.org',
			'client_id' : '821447254243.apps.googleusercontent.com',
			'scope' : 'https://www.googleapis.com/auth/buzz',
			'auth_token_endpoint' : 'http://tbecker2.dyndns.org:80/oauth'
		};

		var redirect_url = "http://" + settings.domain + "/oauthcallback";
		var authUrl = "https://accounts.google.com/o/oauth2/auth?response_type=token&client_id="
				+ settings.client_id
				+ "&redirect_uri="
				+ window.location
				+ "&scope=" + settings.scope;

		var token = extractToken(document.location.hash);
		if (token) {
			$('div.authenticated').show();
			$('span.token').text(token);
		} else {
			$('div.authenticate').show();
			$("a.connect").attr("href", authUrl);
		}

		function _connectionEstablished() {
			$('span.cometd_connection').text('CometD Connection Established');
		}

		function _connectionBroken() {
			$('span.cometd_connection').text('CometD Connection Broken');
		}

		function _connectionClosed() {
			$('span.cometd_connection').text('CometD Connection Closed');
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
								'<div>Server Says: ' + message.data.greeting
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

		cometd.handshake({
			ext : {
				authentication : {
					oauthAccessToken : extractToken(document.location.hash)
				}
			}
		});
	});
</script>

<style>
.hidden {
  display: none;
}
</style>
</head>

<body>
  <div class="authenticate hidden">
    <a
      class="connect"
      href="">Connect</a>
  </div>

  <div class="authenticated hidden">
    <p>
      You are using token "<span class="token">[no token]</span>".
    </p>
  </div>

  <div>
    <span class=cometd_connection>[empty]</span>
  </div>
</body>
</html>
<body>

  <div id="body"></div>

</body>
</html>
