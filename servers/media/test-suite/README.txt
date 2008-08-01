MMS Load Testing
_______________________________

Note : This is still work under progress.

The architecture is such that multiple User Agent's are created (user.agents property of mmsloadtest.properties) which instantiates the IVREndpoint and binds it to IPAddress specified in 'endpoint.bind.address'. These UA's sends the 'CreateConnection' request to MGCP Resource Adaptor deployed on server which in turn creates the connection at server end and replies back. 

As soon as the 'CreateConnectionResponse' arrives the IVREndpoint starts the Announcement and also starts recording simultaneously.

make sure that you have configured 'audio.file' properly to point to correct .wav file