import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TimerTask;

import javax.swing.Timer;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JTextArea; 
import javax.swing.JScrollPane;

public class Presentador extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textbingo;
    private JButton btnStart;
    private JButton btnStop;

    private int num = 0;
    private int[] numhis = new int[90];
    private JTextArea textnums; 
    private JScrollPane scrollPane; 
    private static PrintWriter writer; 
    private String filename = "\\\\192.168.0.29\\bingo\\numeros.txt";
    private static Presentador presentadorInstance;

    public static void main(String[] args) {
        try {
            writer = new PrintWriter("\\\\192.168.0.29\\bingo\\estado.txt");
            writer.println("true");
            writer.close(); 
        } catch (FileNotFoundException e ) {
        	System.out.println("Error");
            JOptionPane.showMessageDialog( presentadorInstance,"Error al conectar a la database");
            System.exit(0);
        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Presentador frame = new Presentador();
                    frame.setVisible(true);
                    presentadorInstance = frame; 
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkFileForLinea();
                checkFileForBingo();
            }
        });
        timer.start();
    }

    public Presentador() {
        setDefaultLookAndFeelDecorated(true);
        setTitle("Bingo Presenter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 645, 418);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        textbingo = new JTextField();
        textbingo.setEditable(false);
        textbingo.setBackground(new Color(255, 204, 102));
        textbingo.setHorizontalAlignment(SwingConstants.CENTER);
        textbingo.setText("------");
        textbingo.setFont(new Font("Tahoma", Font.PLAIN, 95));
        textbingo.setBounds(197, 109, 224, 142);
        contentPane.add(textbingo);
        textbingo.setColumns(10);

        JLabel lblNewLabel = new JLabel("BINGO RAXTECH");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 35));
        lblNewLabel.setBounds(175, 11, 279, 43);
        contentPane.add(lblNewLabel);

        btnStart = new JButton("Empezar");
        btnStart.setBounds(131, 303, 89, 23);
        contentPane.add(btnStart);

        btnStop = new JButton("Salir");
        btnStop.setBounds(416, 303, 89, 23);
        contentPane.add(btnStop);

        JLabel lblNewLabel_1 = new JLabel("Numeros salidos:");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblNewLabel_1.setBounds(496, 40, 109, 14);
        contentPane.add(lblNewLabel_1);

        textnums = new JTextArea();
        textnums.setTabSize(10);
        textnums.setEditable(false);
        textnums.setLineWrap(false);
        textnums.setWrapStyleWord(true);
        textnums.setBounds(10, 11, 58, 307);
        contentPane.add(textnums);
        textnums.setColumns(10);

        scrollPane = new JScrollPane(textnums);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(575, 61, 54, 307);
        contentPane.add(scrollPane);

        eventos();

        try {
            writer = new PrintWriter(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void eventos() {
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int delay = 5000; // delay
                Timer timer = new Timer(delay, new ActionListener() {
                    private int cont = 0;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (cont < 90) {
                            num = generar(num);
                            numhis[cont] = num;
                            textbingo.setText(Integer.toString(num));
                            textnums.append(Integer.toString(num) + "\n");
                            imprimir(Integer.toString(num));
                            cont++;
                        } else {
                            ((Timer) e.getSource()).stop();
                        }
                    }

                    private void imprimir(String numberToSave) {
                        writer.println(numberToSave);
                        writer.flush();
                    }
                });

                timer.start();
            }
        });



        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(Presentador.this, "¿Estás seguro que quieres salir?", "Aviso",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    writer.close(); 
                    writeFalseToEstadoFile(); 
                    System.exit(0);
                }
            }
        });
    }

    private void writeFalseToEstadoFile() {
        try (PrintWriter estadoWriter = new PrintWriter("\\\\192.168.0.29\\bingo\\estado.txt")) {
            estadoWriter.println("false");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void checkFileForLinea() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("\\\\192.168.0.29\\bingo\\bl.txt"));
            String line;
            String nombre = null; // Initialize nombre to null

            // Now, read nombre.txt and store its content in the 'nombre' string
            try {
                BufferedReader nombreReader = new BufferedReader(new FileReader("\\\\192.168.0.29\\bingo\\nomlin.txt"));
                StringBuilder nombreContent = new StringBuilder();
                String nombreLine;

                while ((nombreLine = nombreReader.readLine()) != null) {
                    nombreContent.append(nombreLine);
                }

                nombreReader.close();
                nombre = nombreContent.toString();
            } catch (IOException e) {
                e.printStackTrace();
               
            }



            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains("linea")) {
                    if (presentadorInstance != null) {
                        int choice = JOptionPane.showConfirmDialog(presentadorInstance, "Hay lineaaa!!! Por parte de: " + nombre + " :0", "Alerta", JOptionPane.OK_OPTION);
                       
                        if (choice == JOptionPane.OK_OPTION) {
                            writeNullToBlFile();
                        }
                    }
                }
            }
            reader.close();
            
           
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void writeNullToBlFile() {
        try (PrintWriter blWriter = new PrintWriter("\\\\192.168.0.29\\bingo\\bl.txt")) {
            blWriter.println("null");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void checkFileForBingo() {
       try {
    	BufferedReader reader = new BufferedReader(new FileReader("\\\\192.168.0.29\\bingo\\bl.txt"));
        String line;
        String nombre = null; 
        
        try {
            BufferedReader nombreReader = new BufferedReader(new FileReader("\\\\192.168.0.29\\bingo\\nombin.txt"));
            StringBuilder nombreContent = new StringBuilder();
            String nombreLine;

            while ((nombreLine = nombreReader.readLine()) != null) {
                nombreContent.append(nombreLine);
            }

            nombreReader.close();
            nombre = nombreContent.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        

        while ((line = reader.readLine()) != null) {
            if (line.toLowerCase().contains("bingo")) {
                if (presentadorInstance != null) {
                      JOptionPane.showMessageDialog(presentadorInstance, "Bingooo el ganador es " + nombre + " !! Felicidades");
                        writeNullToBlFile();
                        System.exit(0);
                    
                }
            }
        }
        reader.close();
        
        
    } catch (IOException e) {
        e.printStackTrace();
        }
    
    }


    
    
    
    protected int generar(int num) {
        int nuevoNumero;
        do {
            nuevoNumero = (int) (Math.random() * 90 + 1);
        } while (numeroRepetido(nuevoNumero));
        return nuevoNumero;
    }

    private boolean numeroRepetido(int numero) {
        for (int i = 0; i < numhis.length; i++) {
            if (numhis[i] == numero) {
                return true;
            }
        }
        return false;
    }
}

