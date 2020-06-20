# JavaEncryptDecrypt
You can encrypt and decrypt messages from comand line inline or file 



java Main.java -key 5 -alg shift -data "Welcome to hyperskill!" -mode enc


arguments:

-mode enc   => encrypt
-mode dec   => decrypt

-key int => an integer that will be used in encoding/decoding process

-data String => String input message from inline command line (to select input message from txt file, leave it blank)

-in fileName.txt => full name of txt file to pass input message

-out fileName.txt => full name of txt file to pass output message

-alg shift => program will use shift method to encode/decode message

-alg unicode => program will use unicode method to encode/decode message