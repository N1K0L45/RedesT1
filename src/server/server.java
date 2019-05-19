import java.net.*;
import java.sql.Timestamp;
import java.io.*;
import java.util.Scanner;

public class server {

	public static void main(String[] args) throws IOException{
		try{
			int portnum = 1234;

			System.out.println("Esperando conexion con el cliente...");

			ServerSocket s = new ServerSocket(portnum);
			while(true){
				Socket socketthread = s.accept();
				new ThreadSocket(socketthread);
				System.out.println("Cliente conectado!");
			}
		}catch(Exception e){
		}
}
}
class ThreadSocket extends Thread{
	private Socket ss;
	ThreadSocket(Socket sockin){
		this.ss = sockin;
		this.start();
	}
	@Override
	public void run(){
		try{
			String regexGET = "(get\\s)([A-Z|a-z|_|,|-|0-9]+)(\\.)([A-Z|a-z|0-9]+)"; //Expresiones regulares para los comandos
			String regexPUT = "(put\\s)([A-Z|a-z|_|,|-|0-9]+)(\\.)([A-Z|a-z|0-9]+)";
			String regexDEL = "(delete\\s)([A-Z|a-z|_|,|-|0-9]+)(\\.)([A-Z|a-z|0-9]+)";

			File log = new File("log.txt");  //Archivo log.txt, donde se llevara registro de las acciones
			String header = "DATETIME\t\tEVENT\t\tDESCRIPTION\n";
			BufferedWriter hdr = new BufferedWriter(new FileWriter(log));
			hdr.write(header);
			hdr.close();

			Timestamp time = new Timestamp(System.currentTimeMillis());	// Se hace la entrada de conexion a log.txt
			String socketIP = ss.getRemoteSocketAddress().toString();
			String conex = "\n"+time+"\tconnection\t"+socketIP+" conexion entrante\n";
			BufferedWriter w = new BufferedWriter(new FileWriter(log,true));
			w.newLine();
			w.write(conex);
			w.close();


			BufferedReader br = new BufferedReader(new InputStreamReader(ss.getInputStream()));
			String opcion = br.readLine();
			while(true) {

				if(opcion.equals("ls")) {  //Comando ls
					time = new Timestamp(System.currentTimeMillis());	//Se registra en log.txt
					String logear = "\n"+time+"\tcommand\t\t"+socketIP+" ls\n";
					w = new BufferedWriter(new FileWriter(log,true));
					w.newLine();
					w.write(logear);
					w.close();


					String cwd = System.getProperty("user.dir");
					File dir = new File(cwd);
					String[] paths = dir.list();
					PrintWriter writer = new PrintWriter(ss.getOutputStream(),true);
					for(String path : paths) {
						writer.println(path);
					}
					time = new Timestamp(System.currentTimeMillis());	//registro en log.txt
					logear = "\n"+time+"\tresponse\t"+"servidor envia respuesta a "+socketIP+"\n";
					w = new BufferedWriter(new FileWriter(log,true));
					w.newLine();
					w.write(logear);
					w.close();

					opcion = "a";
					//break;
			}
				else if(opcion.matches(regexGET)) {	//Comando GET
					String[] xp = opcion.split("\\s");
					String nombre_archivo = xp[1];
					System.out.println("nombre_archivo: "+nombre_archivo);

					time = new Timestamp(System.currentTimeMillis());  //registro en log.txt
					String logear = "\n"+time+"\tcommand\t\t"+socketIP+" get "+nombre_archivo+"\n";
					w = new BufferedWriter(new FileWriter(log,true));
					w.newLine();
					w.write(logear);
					w.close();

					String cwd = System.getProperty("user.dir");
					System.out.println("CWD: "+cwd);
					File dir = new File(cwd);
					File[] list = dir.listFiles();
					if(list!=null) {
						for(File fil : list) {
							System.out.println(fil.getName());
							if(nombre_archivo.equals(fil.getName())) {
								String file2send = cwd+"/"+nombre_archivo;
								System.out.println("file2send: "+file2send);
								File myFile = new File(file2send);
								byte[] mybytearray = new byte[(int)myFile.length()];

								int size = mybytearray.length;
								PrintWriter writer = new PrintWriter(ss.getOutputStream(),true);
								writer.println(size);


								FileInputStream fis = new FileInputStream(myFile);
								BufferedInputStream bis = new BufferedInputStream(fis);
								bis.read(mybytearray,0,mybytearray.length);
								OutputStream os = ss.getOutputStream();
								System.out.println("Enviando "+file2send+" ("+mybytearray.length+" bytes)");
								os.write(mybytearray,0,mybytearray.length);
								os.flush();
								System.out.println("Listo!");
								bis.close();
								os.close();


								time = new Timestamp(System.currentTimeMillis());	//registro en log.txt
								logear = "\n"+time+"\tresponse\t"+"servidor envia respuesta a "+socketIP+"\n";
								w = new BufferedWriter(new FileWriter(log,true));
								w.newLine();
								w.write(logear);
								w.close();
								break;
							}
						}
					}
				}

				else if(opcion.matches(regexPUT)) {	//Comando PUT
					String[] xp = opcion.split("\\s");
					int current = 0;
					String nombre_archivo = xp[1];
					String cwd = System.getProperty("user.dir");
					String file2receive = cwd+"/uploaded_"+nombre_archivo;

					time = new Timestamp(System.currentTimeMillis());	//registro en log.txt
					String logear = "\n"+time+"\tcommand\t\t"+socketIP+" put "+nombre_archivo+"\n";
					w = new BufferedWriter(new FileWriter(log,true));
					w.newLine();
					w.write(logear);
					w.close();

					BufferedReader iwi = new BufferedReader(new InputStreamReader(ss.getInputStream()));
					String fileS = iwi.readLine();
					int fileSize = Integer.parseInt(fileS);
					System.out.println("Tamanno del archivo: "+fileSize);

					byte[] mybytearray = new byte[6022386];
					InputStream is = ss.getInputStream();
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

					time = new Timestamp(System.currentTimeMillis());	//registro en log.txt
					logear = "\n"+time+"\tresponse\t"+"servidor envia respuesta a "+socketIP+"\n";
					w = new BufferedWriter(new FileWriter(log,true));
					w.newLine();
					w.write(logear);
					w.close();
					break;
				}

				else if(opcion.matches(regexDEL)) {	//Comando DELETE
					String[] xp = opcion.split("\\s");
					String nombre_archivo = xp[1];

					time = new Timestamp(System.currentTimeMillis());	//registro en log.txt
					String logear = "\n"+time+"\tcommand\t\t"+socketIP+" delete "+nombre_archivo+"\n";
					w = new BufferedWriter(new FileWriter(log,true));
					w.newLine();
					w.write(logear);
					w.close();

					String cwd = System.getProperty("user.dir");
					String file2del = cwd+"/"+nombre_archivo;
					File f = new File(file2del);

					PrintWriter writer = new PrintWriter(ss.getOutputStream(),true);
					if(f.delete()) {
						writer.println("Se ha eliminado "+nombre_archivo);
					}
					else {
						writer.println("El archivo no existe");
					}

					time = new Timestamp(System.currentTimeMillis());	//registro en log.txt
					logear = "\n"+time+"\tresponse\t"+"servidor envia respuesta a "+socketIP+"\n";
					w = new BufferedWriter(new FileWriter(log,true));
					w.newLine();
					w.write(logear);
					w.close();
					break;
				}

				else if(opcion.equals("salir")){
					System.out.println("Chau");
					break;
				}
				else {
					continue;
				}
			}


		}catch(Exception e){
		}
	}
}
