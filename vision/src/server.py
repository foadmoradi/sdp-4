import signal, os, threading
import SocketServer
import worldstate
import worldstate_pb2

class WorldStateRequestHandler(SocketServer.BaseRequestHandler):
    def setup(self):
		pass

    def handle(self):
        data = self.request.recv(1024)
        request = worldstate_pb2.WorldStateRequest()
        request.ParseFromString(data)
	self.request.send(worldstate.WorldState.serialise())
	

    def finish(self):
		pass

class Server(threading.Thread):
    def __init__(self):
	#server host is a tuple ('host', port)
	self.server = SocketServer.ThreadingTCPServer(('localhost', 50008), WorldStateRequestHandler)
	threading.Thread.__init__ ( self )
	
    def run(self):
	self.server.serve_forever()

def handler(signum, frame):
    print '\nServer exiting...'
    exit()

# Set the signal handler and a 5-second alarm
signal.signal(signal.SIGINT, handler)
