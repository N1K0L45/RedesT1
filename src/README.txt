Tarea 1 Redes

Nicolás Acevedo 201573512-3

Para compilar la tarea, se debe estar en el directorio "src" por consola,
y ejecutar el comando "make".

*Si no funciona make, debera realizarse lo siguiente:
	-Si es servidor, ejecutar por consola "javac server.java" para compilar
	-Luego, ejecutarlo con "java server"
	
	•Si es cliente, compilar con "javac client.java"
	•Luego, ejecutar con "java client"

Primero debe hacer un servidor y correrlo. Se le pedira ingresar el numero de puerto.
Debe ingresar el puerto que desee, pero en el codigo del cliente esta definido como
el puerto 1234.

*Cabe destacar que la IP está configurada en el archivo client.java como "127.0.0.1" (localhost).
 Debe configurarse manualmente (linea 21, se encuentra comentada).

Una vez que el servidor esta esperando conexion, se ejecuta client. Aqui debe poner el
comando que desea ejecutar.

El comando debe escribirse en minuscula, sin espacios extra, y solo siguiendo el formato
en el que es presentado en la consola.

Al momento de hacer get o put, el archivo debe existir, o no funcionara. NO sucede lo mismo con delete.

*El programa cierra los sockets despues de cada comando, por lo que debe volver a ejecutar los programas
 para probar las otras funciones :(

*El programa funciona sin ThreadPool :(
