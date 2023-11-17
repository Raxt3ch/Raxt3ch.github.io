import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import javax.swing.JTextField;

public class Carton extends JFrame {
    private JPanel txtnombre;
    private JButton[][] nums = new JButton[3][9];
    private JButton btnbingo;
    private JButton btnlinea;
    private JButton btnSal;
    private static String nombrelin = "anonymous";
    private static PrintWriter writer;
    private JTextField textnombre;
    private List<Integer> uniqueNumbers;
    private CheckFileThread fileThread;
    private static Carton cartonInstance;

    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("\\\\192.168.0.29\\bingo\\estado.txt"));
            String estado = br.readLine();
            br.close();

            if ("true".equals(estado.trim())) {
                EventQueue.invokeLater(() -> {
                    try {
                        Carton frame = new Carton();
                        frame.setVisible(true);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Error al iniciar la aplicación");
                        e.printStackTrace();
                        System.exit(0);
                    }
                });
            } else {
                JOptionPane.showMessageDialog(null, "El programa todavía no se ha iniciado");
                System.exit(0);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al conectar al archivo '\\\\192.168.0.29\\bingo\\estado.txt'");
        }
    }

    public Carton() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1326, 469);
        txtnombre = new JPanel();
        txtnombre.setBackground(new Color(30, 144, 255));
        txtnombre.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(txtnombre);
        txtnombre.setLayout(null);

        int buttonCount = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                nums[i][j] = new JButton("");
                nums[i][j].setFont(new Font("Tahoma", Font.BOLD, 35));
                nums[i][j].setBounds(10 + j * 110, 56 + i * 101, 100, 90);
                txtnombre.add(nums[i][j]);
                buttonCount++;
            }
        }

        uniqueNumbers = llenarYOrdenarNumeros();

        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < nums[i].length; j++) {
                nums[i][j].setText(uniqueNumbers.get(i * nums[i].length + j).toString());
            }
        }

        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < nums[i].length; j++) {
                if (!nums[i][j].isEnabled()) {
                    nums[i][j].setText("");
                }
            }
        }

        JLabel lblNewLabel = new JLabel("Bingo ALMINGO !!!");
        lblNewLabel.setForeground(new Color(255, 250, 250));
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setBounds(39, 11, 1233, 32);
        txtnombre.add(lblNewLabel);

        btnbingo = new JButton("Bingoo");
        btnbingo.setFont(new Font("Tahoma", Font.PLAIN, 18));
        btnbingo.setBounds(1093, 58, 103, 77);
        txtnombre.add(btnbingo);

        btnlinea = new JButton("Linea");
        btnlinea.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnlinea.setBounds(1093, 146, 103, 77);
        txtnombre.add(btnlinea);

        btnSal = new JButton("SALIR");
        btnSal.setForeground(new Color(192, 192, 192));
        btnSal.setBackground(new Color(128, 0, 0));
        btnSal.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnSal.setBounds(1169, 323, 103, 53);
        txtnombre.add(btnSal);

        JLabel lblNewLabel_1 = new JLabel("Bienvenido");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblNewLabel_1.setBounds(10, 11, 72, 14);
        txtnombre.add(lblNewLabel_1);

        textnombre = new JTextField();
        textnombre.setEditable(false);
        textnombre.setFont(new Font("Tahoma", Font.BOLD, 14));
        textnombre.setBackground(new Color(0, 128, 255));
        textnombre.setBounds(78, 7, 244, 20);
        txtnombre.add(textnombre);
        textnombre.setColumns(10);

        nombrelin = JOptionPane.showInputDialog("Pon el nombre del jugador");

        if (nombrelin == null || nombrelin.trim().isEmpty()) {
            nombrelin = "anonymous" + (int) (Math.random() * 1000 + 1);
        }
        textnombre.setText(nombrelin);

        eventos();

        fileThread = new CheckFileThread(this);
        fileThread.start();
    }

    private List<Integer> llenarYOrdenarNumeros() {
        int[][] carton = new int[3][9];
        ArrayList<Integer> numerosGenerados = new ArrayList<>();
        List<Integer> uniqueNumbers = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            int disabledCount = 0; // Counter for disabled buttons in the row
            for (int j = 0; j < 9; j++) {
                int n;
                do {
                    switch (j) {
                        case 0:
                            n = aleatorio(1, 9);
                            break;
                        case 8:
                            n = aleatorio(80, 90);
                            break;
                        default:
                            n = aleatorio(10 * j, (10 * j) + 9);
                            break;
                    }
                } while (numerosGenerados.contains(n));

                numerosGenerados.add(n);
                carton[i][j] = n;
                uniqueNumbers.add(n);

                
                if (disabledCount < 4 && Math.random() < 0.5) {
                    nums[i][j].setEnabled(false);
                    nums[i][j].setText(""); 
                    disabledCount++;
                } else {
                    nums[i][j].setEnabled(true);
                    nums[i][j].setText(Integer.toString(n)); 
                }
            }
        }

        for (int j = 0; j < 9; j++) {
            int disabledCount = 0;
            int rowIndexDisabled = -1;
            for (int i = 0; i < 3; i++) {
                if (!nums[i][j].isEnabled()) {
                    disabledCount++;
                    rowIndexDisabled = i;
                }
            }

           
            if (disabledCount > 2 || disabledCount == 0) {
                if (rowIndexDisabled != -1) {
                    nums[rowIndexDisabled][j].setEnabled(false);
                    nums[rowIndexDisabled][j].setText(""); 
                }
            }
        }

        return uniqueNumbers;
    }

    
    private int aleatorio(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    
	private void eventos() {
        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < nums[i].length; j++) {
                nums[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JButton clickedButton = (JButton) e.getSource();
                        if (clickedButton.isEnabled()) {
                            clickedButton.setEnabled(false);
                            int clickedNumber = Integer.parseInt(clickedButton.getText());
                            uniqueNumbers.remove(Integer.valueOf(clickedNumber));
                        }
                    }
                });
            }
        }

        btnSal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(Carton.this, "¿Estás seguro que quieres salir?", "Aviso",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        btnlinea.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               
            	
            	
            	
            	boolean comlin = complinea(nums);
   

                if (comlin) {
                    System.out.println("Hay linea");
                    linea()
;                    
                } else {
                    JOptionPane.showMessageDialog(Carton.this, "No hay linea");
                }
            }
        });

        btnbingo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean combin = false;
                combin = compbingo();

                if (combin) {
                	System.out.println("Hay bingo");
                    bingo();
                } else {
                    JOptionPane.showMessageDialog(Carton.this, "NO Hay bingo intentalo de nuevo");
                }
            }
        });
    }

	protected boolean complinea(JButton[][] bingoBoard) {
	    String fileName = "\\\\192.168.0.29\\bingo\\numeros.txt";
	    List<Set<String>> lines = new ArrayList<>();

	    // Initialize sets for each line
	    for (int i = 0; i < bingoBoard.length; i++) {
	        lines.add(new HashSet<String>());
	    }

	    try {
	        File file = new File(fileName);
	        Scanner scanner = new Scanner(file);

	        while (scanner.hasNextLine()) {
	            String line = scanner.nextLine();
	            String[] numbersInFile = line.trim().split("\\s+");

	            // Add numbers from the file to sets for each line
	            for (int i = 0; i < bingoBoard.length; i++) {
	                Collections.addAll(lines.get(i), numbersInFile);
	            }
	        }
	        scanner.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	        return false; // Error reading file
	    }

	    int completeLinesCount = 0;

	    // Validate each line in the bingo board
	    for (int i = 0; i < bingoBoard.length; i++) {
	        Set<String> currentLineNumbers = new HashSet<>();
	        for (JButton button : bingoBoard[i]) {
	            if (!button.getText().isEmpty()) {
	                currentLineNumbers.add(button.getText());
	            }
	        }

	        // Check if all numbers in the current line are in the corresponding set from the file
	        if (lines.get(i).containsAll(currentLineNumbers)) {
	            completeLinesCount++;
	        }
	    }

	    return completeLinesCount > 0; // Return true if at least one line is complete
	}


	protected boolean compbingo() {
	    String fileName = "\\\\192.168.0.29\\bingo\\numeros.txt";
	    Set<String> allNumbers = new HashSet<>();

	    // Lee todos los números del archivo y los agrega a un conjunto
	    try {
	        File file = new File(fileName);
	        Scanner scanner = new Scanner(file);

	        while (scanner.hasNextLine()) {
	            String line = scanner.nextLine();
	            String[] numbersInFile = line.trim().split("\\s+");
	            Collections.addAll(allNumbers, numbersInFile);
	        }
	        scanner.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	        return false; // Error al leer el archivo
	    }

	    // Verifica si todos los números mostrados están presentes en el conjunto de números del archivo
	    for (JButton[] row : nums) {
	        for (JButton button : row) {
	            String buttonText = button.getText();
	            if (!buttonText.isEmpty() && !allNumbers.contains(buttonText)) {
	                return false; // Encontró un número que no está presente en el archivo
	            }
	        }
	    }

	    return true; // Todos los números mostrados están en el archivo
	}


	public static void bingo() {
	    try {
	        boolean haybin = false;
	        int cont = 0;

	        while (true) {
	            haybin = preguntabin(); // Aquí deberías tener una función para preguntar si hay bingo
	            cont++;

	            if (haybin) {
	                try (PrintWriter writerBl = new PrintWriter("\\\\192.168.0.29\\bingo\\bl.txt");
	                     PrintWriter writerNomlin = new PrintWriter("\\\\192.168.0.29\\bingo\\nombin.txt")) {

	                    writerBl.println("bingo");
	                    writerNomlin.println(nombrelin);

	                } catch (FileNotFoundException e) {
	                    e.printStackTrace();
	                }
	                break;
	            }

	            if (cont == 3) {
	                JOptionPane.showMessageDialog(cartonInstance, "Has superado el límite de preguntas");
	                System.exit(0);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public static void linea() {
	    try {
	        int cont = 0;
	        boolean haylin = false; 

	        while (true) { 
	            haylin = preguntalin();
	            cont++;

	            if (haylin) { 
		            try (PrintWriter writerBl = new PrintWriter("\\\\192.168.0.29\\bingo\\bl.txt");
			                 PrintWriter writerNomlin = new PrintWriter("\\\\192.168.0.29\\bingo\\nomlin.txt")) {

			                writerBl.println("linea");
			                writerNomlin.println(nombrelin);
			                
			            } catch (FileNotFoundException e) {
			                e.printStackTrace();
			            }
		            break;
		            
	            }
	            if (cont == 3) {
		            JOptionPane.showMessageDialog(cartonInstance, "Has superado el limite de preguntas");
		            System.exit(0);
		        }
	         
	        }

	       

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


    class CheckFileThread extends Thread {
        private volatile boolean running = true;
        private Carton carton;

        public CheckFileThread(Carton carton) {
            this.carton = carton;
        }

        public void stopThread() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(500); 

                    BufferedReader br = new BufferedReader(new FileReader("\\\\192.168.0.29\\bingo\\bl.txt"));
                    String content = br.readLine();
                    br.close();

                    if (content != null) {
                        switch (content) {
                            case "bingo":
                                carton.ayBingo(); 
                                break;
                            case "linea":
                                carton.ayLinea(); 
                                break;
                            default:
                                
                                break;
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void ayBingo() {
        String nomlin = ""; // Variable para guardar el contenido del archivo

        try {
            BufferedReader br = new BufferedReader(new FileReader("\\\\192.168.0.29\\bingo\\nombin.txt"));
            nomlin = br.readLine(); // Lee la primera línea del archivo
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(Carton.this, "Hay bingo!!  El ganador es: " + nomlin);
        System.exit(0);

        try {
            PrintWriter writer = new PrintWriter("\\\\192.168.0.29\\bingo\\bl.txt", "UTF-8");
            writer.print("null"); // Escribe "null" en el archivo
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	/**
	 * 
	 */
	public void ayLinea() {
	    String nomlin = ""; // Variable para guardar el contenido del archivo

	    try {
	        BufferedReader br = new BufferedReader(new FileReader("\\\\192.168.0.29\\bingo\\nomlin.txt"));
	        nomlin = br.readLine(); // Lee la primera línea del archivo
	        br.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    JOptionPane.showMessageDialog(Carton.this, "Hay linea por parte del: " + nomlin);
	    btnlinea.setEnabled(false);

	    try {
	        PrintWriter writer = new PrintWriter("\\\\192.168.0.29\\bingo\\bl.txt", "UTF-8");
	        writer.print("null"); // Escribe "null" en el archivo
	        writer.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
    
	
	
	public static boolean preguntalin(){

		String res ;
		 
		
		String[] preguntas = {
			    "¿Cuál de los siguientes derechos es un derecho fundamental?\n" +
			    "a) Derecho a la huelga.\n" +
			    "b) Bono de rendimiento.",

			    "Todos los empleados tienen derecho a coche de empresa:\n" +
			    "a) Verdadero.\n" +
			    "b) Falso.",

			    "Duración máxima de la jornada semanal en cómputo anual:\n" +
			    "a) 35 horas a la semana.\n" +
			    "b) 40 horas a la semana.",

			    "¿Cuál es la duración mínima del período de descanso semanal?\n" +
			    "a) 24 horas consecutivas.\n" +
			    "b) 36 horas consecutivas.",

			    "¿Si un empleado trabaja horas extras, tiene que ser compensado de alguna manera?\n" +
			    "a) Si, con una retribución salarial o con días de descanso.\n" +
			    "b) Si, con ventajas exclusivas en la empresa.",

			    "El periodo de prueba es obligatorio:\n" +
			    "a) Verdadero\n" +
			    "b) Falso",

			    "¿Cuál es el plazo para presentar una reclamación laboral en caso de despido?\n" +
			    "a) 10 días hábiles.\n" +
			    "b) 20 días hábiles.",

			    "¿Cuál de las siguientes afirmaciones representa un derecho laboral?\n" +
			    "a) Trabajar horas extras sin remuneración adicional.\n" +
			    "b) Recibir un salario justo y condiciones de trabajo seguras.",

			    "¿Cuál de las siguientes afirmaciones refleja un deber laboral importante?\n" +
			    "a) Ignorar las instrucciones del supervisor.\n" +
			    "b) Cumplir con las responsabilidades asignadas en el trabajo de manera diligente.",

			    "¿Quién está obligado a pagar las cotizaciones a la Seguridad Social?\n" +
			    "a) Solo el empleado.\n" +
			    "b) Ambos, el empleado y el empleador.",

			    "¿Cuál es la duración mínima del período de descanso entre dos jornadas?\n" +
			    "a) 9 horas.\n" +
			    "b) 12 horas.",

			    "¿Cuánto tiempo se otorga con el permiso de paternidad?\n" +
			    "a) 4 meses.\n" +
			    "b) 16 semanas.",

			    "¿Cuál es el derecho de los trabajadores en caso de despido injustificado?\n" +
			    "a) Derecho a una bonificación.\n" +
			    "b) Derecho a una indemnización justa.",

			    "¿Cuál de las siguientes afirmaciones es cierta sobre el salario mínimo interprofesional (SMI)?\n" +
			    "a) Varía según la experiencia laboral del empleado.\n" +
			    "b) Es una cantidad establecida por el gobierno.",

			    "Duración mínima de las vacaciones anuales:\n" +
			    "a) 20 días.\n" +
			    "b) 30 días.",

			    "La baja por enfermedad está completamente pagada desde el primer día:\n" +
			    "a) Verdadero\n" +
			    "b) Falso",

			    "¿En qué circunstancias un empleado puede ser despedido sin previo aviso ni indemnización?\n" +
			    "a) En casos de mala conducta grave.\n" +
			    "b) No se puede despedir a un empleado sin previo aviso ni indemnización.",

			    "¿Cuál es la edad mínima para trabajar, según la legislación laboral?\n" +
			    "a) 14 años.\n" +
			    "b) 16 años.",

			    "¿El empleado tiene derecho a unirse a un sindicato?\n" +
			    "a) Si.\n" +
			    "b) No.",

			    "¿En qué casos un empleado puede solicitar una excedencia voluntaria?\n" +
			    "a) Cuando tenga una antigüedad mínima de un año.\n" +
			    "b) En cualquier momento, siempre que lo comunique con suficiente antelación.",

			    "¿Que es un contrato temporal?\n" +
			    "a) Un contrato de trabajo que se establece por un tiempo determinado.\n" +
			    "b) Un contrato de trabajo que se establece de manera indefinida y no tiene una fecha de finalización.",

			    "¿Cuál es la duración máxima de un contrato de alternancia?\n" +
			    "a) 2 años.\n" +
			    "b) 3 años.",

			    "¿Un empleado puede negarse a trabajar horas extraordinarias si lo hace con una razón justificada?\n" +
			    "a) Si.\n" +
			    "b) No.",

			    "¿Cuántos días de permiso por matrimonio se otorgan a un empleado?\n" +
			    "a) 7 días naturales.\n" +
			    "b) 15 días naturales.",

			    "¿Qué se entiende por contrato de trabajo a tiempo parcial?\n" +
			    "a) Un contrato en el que el empleado trabaja menos de la jornada completa de un trabajador a tiempo completo comparable.\n" +
			    "b) Un contrato en el que el empleado trabaja exactamente la mitad del tiempo de un empleado a tiempo completo."
			};

		 
		 
		
				 
		 String[] respuestas = {"a", "b", "b", "b", "a", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "a", "b", "a", "a", "a", "a", "a", "b", "a"};
		 
		 
		 int aux= (int) (Math.random()*25+1);
		

	res = JOptionPane.showInputDialog(preguntas[aux]);
	
	if (res.equalsIgnoreCase(respuestas[aux])){
		JOptionPane.showMessageDialog(cartonInstance, "Correcto tiene linea");
		return true;
		
	}else {
		JOptionPane.showMessageDialog(cartonInstance, "Incrroecto preuba de nuevo");
		return false;
	}
		
	}
    
	
	
	public static boolean preguntabin(){

		String res ;
		
		String[] preguntas = {
			    "Las embarazadas tienen derecho a medidas especiales de protección durante el embarazo y la maternidad:\n" +
			    "a) Verdadero.\n" +
			    "b) Falso.\n" +
			    "c) Sólo si ha habido complicaciones en el parto.",

			    "¿Cuál es la duración máxima de un contrato de interinidad?\n" +
			    "a) 3 meses.\n" +
			    "b) 6 meses.\n" +
			    "c) 12 meses.",

			    "¿Cuál es el salario mínimo interprofesional (SMI) para el año 2023?\n" +
			    "a) 950 euros al mes.\n" +
			    "b) 1.080 euros al mes.\n" +
			    "c) 1.200 euros al mes.",

			    "¿Qué implica el derecho a la intimidad?\n" +
			    "a) Acceso libre a información personal.\n" +
			    "b) Respeto y protección de la vida privada del empleado.\n" +
			    "c) Supervisión constante sin consentimiento.",

			    "¿Qué representa la sigla SEPE?\n" +
			    "a) Servicio Estatal de Pago Electrónico.\n" +
			    "b) Sistema Empresarial de Prevención y Evaluación.\n" +
			    "c) Servicio Público de Empleo Estatal.",

			    "¿Qué es la Inspección de Trabajo y Seguridad Social?\n" +
			    "a) Una entidad encargada de contratar trabajadores temporales.\n" +
			    "b) Un organismo que supervisa y garantiza el cumplimiento de las normas laborales.\n" +
			    "c) Una organización sindical en España.",

			    "¿Cuál es la duración mínima de un contrato de trabajo?\n" +
			    "a) 1 mes.\n" +
			    "b) 3 meses.\n" +
			    "c) Puede variar según el tipo de contrato y las circunstancias específicas.",

			    "¿Qué derechos tienen los empleados a tiempo parcial en comparación con los empleados a tiempo completo?\n" +
			    "a) Los empleados a tiempo parcial tienen los mismos derechos que los empleados a tiempo completo en todos los aspectos.\n" +
			    "b) Los empleados a tiempo parcial pueden tener diferencias en términos de días de vacaciones y permisos.\n" +
			    "c) Los empleados a tiempo parcial tienen más derechos que los empleados a tiempo completo en términos de flexibilidad laboral y horas de trabajo.",

			    "¿Cuál es el plazo para solicitar una excedencia por cuidado de un familiar?\n" +
			    "a) 5 días antes de necesitarla.\n" +
			    "b) 10 días antes de necesitarla.\n" +
			    "c) No hay un plazo fijo, pero debe notificarse con antelación.",

			    "Un empleado que ha hecho huelga tiene derecho a recibir todo el salario:\n" +
			    "a) Verdadero.\n" +
			    "b) Falso.\n" +
			    "c) Sólo si la huelga dura 2 días.",

			    "¿Cuándo es legal que un empleado haga huelga?\n" +
			    "a) En conflictos colectivos, después de intentos de negociación.\n" +
			    "b) En cualquier momento sin restricciones.\n" +
			    "c) Ante cualquier desacuerdo.",

			    "¿Qué plazo tiene un empleado para solicitar la reincorporación a su puesto de trabajo después de una excedencia?\n" +
			    "a) 15 días antes de finalizar la excedencia.\n" +
			    "b) Dentro del mes siguiente al cese de la causa que lo produjo.\n" +
			    "c) No tiene derecho a reincorporarse.",

			    "¿Cuál es el plazo de preaviso de la baja voluntaria?\n" +
			    "a) 7 días.\n" +
			    "b) 15 días.\n" +
			    "c) 30 días.",

			    "¿Cuál es la duración mínima de un contrato de relevo?\n" +
			    "a) 2 años.\n" +
			    "b) 3 años.\n" +
			    "c) El tiempo que le falte al trabajador que ha solicitado la jubilación anticipada para alcanzar la edad de jubilación ordinaria que corresponda.",

			    "¿En qué situaciones un empleado tiene derecho a una reducción de jornada laboral?\n" +
			    "a) Siempre que lo solicite.\n" +
			    "b) Para el cuidado de un hijo menor de 12 años.\n" +
			    "c) Sólo durante el período de prueba.",

			    "¿Qué es la concatenación de contratos temporales?\n" +
			    "a) La combinación de empleados de distintas empresas.\n" +
			    "b) La sucesión de contratos temporales sin solución de continuidad.\n" +
			    "c) La interrupción sistemática de contratos indefinidos.",

			    "El contrato para la obtención de la práctica profesional está pagado:\n" +
			    "a) Verdadero.\n" +
			    "b) Falso.\n" +
			    "c) Está pagado si el empleado tiene más de 30 años.",

			    "¿Cuál es la duración máxima de un contrato de trabajo de duración determinada por circunstancias de la producción?\n" +
			    "a) 3 meses.\n" +
			    "b) 6 meses.\n" +
			    "c) 1 año.",

			    "¿En qué situaciones un empleado puede ser despedido de manera procedente?\n" +
			    "a) Por cualquier motivo que el empleador considere adecuado.\n" +
			    "b) Por causas objetivas, disciplinarias o económicas.\n" +
			    "c) Siempre que el empleado tenga menos de 2 años de antigüedad.",

			    "¿Qué es el contrato de relevo?\n" +
			    "a) Un contrato a tiempo parcial.\n" +
			    "b) Un contrato que se utiliza para sustituir a un trabajador que ha solicitado la jubilación parcial.\n" +
			    "c) Un contrato que se utiliza para sustituir temporalmente a un empleado en situación de incapacidad.",

			    "¿Cuál es la duración máxima del contrato de alternancia?\n" +
			    "a) 3 meses.\n" +
			    "b) 1 año.\n" +
			    "c) 2 años.",

			    "La baja por enfermedad común es un tipo de baja incapacidad temporal:\n" +
			    "a) Verdadero.\n" +
			    "b) Falso.\n" +
			    "c) Sólo si el empleado es mayor de 30 años.",

			    "¿Cuál es el período de preaviso mínimo que un empleador debe dar a un empleado antes de un despido objetivo?\n" +
			    "a) 15 días.\n" +
			    "b) 30 días.\n" +
			    "c) 60 días.",

			    "¿En qué casos un empleado tiene derecho a un permiso por fallecimiento de un familiar?\n" +
			    "a) Por fallecimiento del cónyuge, pareja de hecho o familiar dentro del primer grado de consanguinidad o afinidad.\n" +
			    "b) Sólo si el empleado es el único responsable del funeral.\n" +
			    "c) En cualquier caso de fallecimiento de un familiar, sin importar el parentesco.",

			    "Con la prevención de riesgos laborales, el empleador debe:\n" +
			    "a) Ignorar las normativas de seguridad.\n" +
			    "b) Proporcionar equipo de protección solo a petición del trabajador.\n" +
			    "c) Garantizar la seguridad y salud en el trabajo mediante medidas preventivas."
			};

		
		
		String[] respuestas = {"a", "c", "b", "b", "c", "b", "c", "a", "c", "b", "a", "b", "b", "c", "b", "b", "b", "b", "b", "b", "c", "a", "a", "a", "c"};

		
		 int aux= (int) (Math.random()*25+1);
			

	res = JOptionPane.showInputDialog(preguntas[aux]);
	
	if (res.equalsIgnoreCase(respuestas[aux])){
		JOptionPane.showMessageDialog(cartonInstance, "Correcto tienes bingo");
		return true;
		
	}else {
		JOptionPane.showMessageDialog(cartonInstance, "Incorrecto prueba de nuevo");
		return false;
	}
	}
	
	
}
