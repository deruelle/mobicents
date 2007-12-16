#!/bin/sh  
KSDEFAULTS="-storepass password -storetype JKS"   
#KEYINFO="-keyalg RSA"   
KEYSIZE="1024"
KEYNAME="mobicents-parlay-ra.jks"
CLIENTID="P_CLIENT_APPLICATION"
CSWAYCERT="CSWAYRSACERTIFICATE.pem"

generateAndExport()
{
#Generate keypair 
 keytool -genkey -alias $CLIENTID -keysize $KEYSIZE -dname "CN=localhost, OU=X, O=Y, L=Z, S=XY, C=YZ" $KSDEFAULTS -keystore $KEYNAME $KEYINFO -keypass password   

#Export client certificate
keytool -export -alias $CLIENTID -file ${CLIENTID}RSACERTIFICATE.pem.der $KSDEFAULTS -rfc -keystore $KEYNAME

menu
}

importCSwayCert()
{
#Import Gateway certificate
echo keytool -import -v -noprompt -alias ${CLIENTID}CSWAY -file $CSWAYCERT $KSDEFAULTS -keystore $KEYNAME

keytool -import -v -noprompt -alias ${CLIENTID}CSWAY -file $CSWAYCERT $KSDEFAULTS -keystore $KEYNAME

#Remove Gateway certificate
#rm $CSWAYCERT 

menu
}


menu() 
{
echo "Press 1 to generate keyPair and export client certificate"
echo "Press 2 to import gateway certificate to keyStore"
echo "x to Exit"

read choice


case $choice in
	1) generateAndExport;;
	2) importCSwayCert;;
	x|X) echo    ''
                        echo    'Exiting'
                        echo    ''
                        exit 0
                        ;;
        esac
}

menu
