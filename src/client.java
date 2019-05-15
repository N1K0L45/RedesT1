import java.net.*;
import java.io.*;
import java.util.Scanner;

public class client {

	public static void main(String[] args) throws IOException{
		int salir = 0;
		int portnum;
		portnum = 1234; 	// portnumber del cliente
		String opIn;
		String regexGET = "(get\\s)([A-Z|a-z|_|,|-]+)(\\.)([A-Z|a-z]+)";	//Expresiones regulares para
		String regexPUT = "(put\\s)([A-Z|a-z|_|,|-]+)(\\.)([A-Z|a-z]+)";	//los metodos get, put y delete
		String regexDEL = "(delete\\s)([A-Z|a-z|_|,|-]+)(\\.)([A-Z|a-z]+)";


		System.out.println("Hola, tengo el puerto "+portnum);

		Scanner in = new Scanner(System.in);			// Se crea el socket, y se configura la IP

/*IPconfig*/Socket cs = new Socket("127.0.0.1",portnum);	// Como se uso el mismo PC para testear el codigo, tiene
														// la IP del localhost
		Scanner in1 = new Scanner(cs.getInputStream());

		Scanner option = new Scanner(System.in);

		while(salir==0) {
			System.out.println("\n---------\nIngresa un comando");	//Menu para el cliente
			System.out.println("Acciones disponibles:\n1. ls\n2. get <nombre_archivo>\n3. put <nombre_archivo>\n4. delete <nombre_archivo>\n5. salir\n");
			opIn = option.nextLine();

			if(opIn.equals("salir")) { // Salir y cerrar la conexion
				PrintWriter cmd = new PrintWriter(cs.getOutputStream(),true);
				cmd.println("salir");
				cmd.close();
				salir=1;
				break;
			}
			else if(opIn.equals("ls")) {	// Comando ls
				System.out.println("-------------------\nListando archivos y directorios:\n");
				PrintWriter cmd = new PrintWriter(cs.getOutputStream(),true);
				cmd.println("ls");

				BufferedReader iwi = new BufferedReader(new InputStreamReader(cs.getInputStream()));
				String lectura;
				while((lectura = iwi.readLine()) != null) {
					System.out.println(lectura);
				}
			}
			else if(opIn.matches(regexGET)) { //Comando GET
				String[] xp = opIn.split("\\s");
				int current = 0;
				String nombre_archivo = xp[1];
				String cwd = System.getProperty("user.dir");
				String file2receive = cwd+"/downloaded_"+nombre_archivo;
				PrintWriter cmd = new PrintWriter(cs.getOutputStream(),true);
				cmd.println(opIn);

				BufferedReader iwi = new BufferedReader(new InputStreamReader(cs.getInputStream()));
				String fileS = iwi.readLine();
				int fileSize = Integer.parseInt(fileS);
				System.out.println("Tamanno del archivo: "+fileSize);

				byte[] mybytearray = new byte[6022386];   // Se asume una cantidad de Bytes fija por esta ocasion
				InputStream is = cs.getInputStream();		// Tamanno maximo archivo: 6.022386 MB
				FileOutputStream fos = new FileOutputStream(file2receive);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				int bytesRead = is.read(mybytearray,0,mybytearray.length);
				current = bytesRead;

				do {
					bytesRead = is.read(mybytearray,current,(mybytearray.length - current));
					if(bytesRead>=0) current += bytesRead;
				}while(bytesRead>-1);

				bos.write(mybytearray,0,current);
				bos.flush();
				System.out.println("Archivo "+file2receive+" descargado ("+current+" bytes)");
				fos.close();
				bos.close();
			}

			else if(opIn.matches(regexPUT)) { //Comando PUT
				String[] xp = opIn.split("\\s");
				String nombre_archivo = xp[1];
				PrintWriter cmd = new PrintWriter(cs.getOutputStream(),true);
				cmd.println(opIn);

				String cwd = System.getProperty("user.dir");
				File dir = new File(cwd);
				File[] list = dir.listFiles();
				if(list!=null) {
					for(File fil : list) {
						System.out.println(fil.getName());
						if(nombre_archivo.equals(fil.getName())) {
							String file2send = cwd+"/"+nombre_archivo;
							File myFile = new File(file2send);
							byte[] mybytearray = new byte[(int)myFile.length()];

							int size = mybytearray.length;
							PrintWriter writer = new PrintWriter(cs.getOutputStream(),true);
							writer.println(size);


							FileInputStream fis = new FileInputStream(myFile);
							BufferedInputStream bis = new BufferedInputStream(fis);
							bis.read(mybytearray,0,mybytearray.length);
							OutputStream os = cs.getOutputStream();
							System.out.println("Enviando "+file2send+" ("+mybytearray.length+" bytes)");
							os.write(mybytearray,0,mybytearray.length);
							os.flush();
							System.out.println("Listo!");
							bis.close();
							os.close();

						}
					}
				}
			}

			else if(opIn.matches(regexDEL)) { //Comando DELETE
				PrintWriter cmd = new PrintWriter(cs.getOutputStream(),true);
				cmd.println(opIn);

				BufferedReader iwi = new BufferedReader(new InputStreamReader(cs.getInputStream()));
				String lectura = iwi.readLine();
				System.out.println(lectura);
			}

			else {
				System.out.println("Ingrese un comando valido");
			}
		}

		in1.close();
		cs.close();
		in.close();
		option.close();
	}

}
