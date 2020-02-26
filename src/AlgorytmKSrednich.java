import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class AlgorytmKSrednich {

    // Algorytm
    private double[][] punkty = null;// [id] - [x][y][nr koloru]
    private double[][] centroidy = null; // [id] - [x][y][nr koloru]

    private Color[] kolory = {Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.GREEN,
            Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED,
            Color.YELLOW, Color.BLACK}; // maksymalnie 11 klastrów

    // GUI
    private JFrame frame;
    private JPanel panelGlowny;
    private JPanel panelDolny;
    private JTextField iloscPunktowTextField;
    private JTextField iloscCentroidowTextField;
    private CustomPanel customPanel; // panel, w którym rysuje punkty
    private JRadioButton euklidesRadioButton = new JRadioButton("Odległość euklidesowa");
    private JRadioButton miejskaRadioButton = new JRadioButton("Odległość miejska");

    public AlgorytmKSrednich() {

        panelGlowny = new JPanel();
        panelGlowny.setLayout(new BorderLayout());

        panelDolny = new JPanel();
        panelDolny.setLayout(new FlowLayout());

        euklidesRadioButton.setSelected(true);

        JLabel iloscPunktowLabel = new JLabel("Ilość punktów: ");
        JLabel iloscCentroidowLabel = new JLabel("Ilość centroidów: ");

        iloscPunktowTextField = new JTextField(2);
        iloscCentroidowTextField = new JTextField(2);

        JButton startButton = new JButton("URUCHOM");

        ButtonGroup group = new ButtonGroup();
        group.add(euklidesRadioButton);
        group.add(miejskaRadioButton);

        startButton.addActionListener(e -> {
            zainicjujWspolrzedne();
            new Thread(() -> startAlgorytm()).start();
            frame.repaint();
        });

        panelDolny.add(euklidesRadioButton);
        panelDolny.add(miejskaRadioButton);
        panelDolny.add(iloscPunktowLabel);
        panelDolny.add(iloscPunktowTextField);
        panelDolny.add(iloscCentroidowLabel);
        panelDolny.add(iloscCentroidowTextField);
        panelDolny.add(startButton);

        customPanel = new CustomPanel();

        panelGlowny.add(panelDolny, BorderLayout.SOUTH);
        panelGlowny.add(customPanel, BorderLayout.CENTER);

        frame = new JFrame("Algorytm k-średnich");

        frame.add(panelGlowny);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setSize(new Dimension(700, 760));
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    class CustomPanel extends JPanel {

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.WHITE);
            g.setColor(Color.BLACK);

            if(punkty != null) {
                for(int i=0; i<punkty.length; i++) {
                    g.setColor(kolory[(int)punkty[i][2]]);
                    g.fillOval((int)punkty[i][0], (int)punkty[i][1], 8, 8);
                    repaint();
                }
            }

            if (centroidy != null) {
                for(int i=0; i<centroidy.length; i++) {
                    g.setColor(kolory[(int)centroidy[i][2]]);
                    g.fillRect((int)centroidy[i][0], (int)centroidy[i][1], 8, 8);
                    repaint();
                }
            }
        }

    }

    private void zainicjujWspolrzedne() { // przedzial (0, 685) [1, 684]

        int iloscPunktow;
        int iloscCentroidow;

        try {
            iloscPunktow = Integer.parseInt(iloscPunktowTextField.getText());
            iloscCentroidow = Integer.parseInt(iloscCentroidowTextField.getText());
            if(iloscCentroidow > 11){
                String st="Maksymalna ilość klastrów to 11!";
                JOptionPane.showMessageDialog(null,st);
                iloscCentroidow=11;
            }

            punkty = new double[iloscPunktow][3];
            centroidy = new double[iloscCentroidow][3];

            Random generatorLiczb = new Random();

            for(int i=0; i<punkty.length; i++) {
                punkty[i][2] = 11; // CZARNY
                for(int j=0; j<punkty[i].length-1; j++) {
                    punkty[i][j] = generatorLiczb.nextInt(685) + 1;
                }
            }

            for(int i=0; i<centroidy.length; i++) {
                centroidy[i][2] = i; // id kolor
                for(int j=0; j<centroidy[i].length-1; j++) {
                    centroidy[i][j] = generatorLiczb.nextInt(685) + 1;
                }
            }

        } catch(NumberFormatException ex) {
            System.out.println(ex);
        }
    }

    private void startAlgorytm() {
    	
        boolean czyZakonczony = false;
        while(!czyZakonczony) {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < punkty.length; i++) {

                double x1 = punkty[i][0];
                double y1 = punkty[i][1];
                double odlegloscMin = 1000; // przykladowa wartosc, nie osiagalna w takim zakresie jak w zadaniu

                for (int j = 0; j < centroidy.length; j++) {

                    double x2 = centroidy[j][0];
                    double y2 = centroidy[j][1];
                    double odleglosc;

                    if (euklidesRadioButton.isSelected()) {
                        odleglosc = Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));//wzór: (x1-x2)^2 + (y1-y2)^2
                    } else {
                        odleglosc = Math.abs(x1 - x2) + Math.abs(y1 - y2);//wzór wartość bezwzględna x1-x2 + wartość bezwzględna y1-y2
                    }

                    if (odleglosc < odlegloscMin) { // przypisanie punktów do centroidow (kolory)
                        odlegloscMin = odleglosc;
                        punkty[i][2] = centroidy[j][2]; // przypisanie centroidu do punktu
                    }
                }
            }

            // wyliczamy nowe centroidy

            for (int i = 0; i < centroidy.length; i++) {

                int iloscPunktow = 0;
                double x = 0;
                double y = 0;

                for (int j = 0; j < punkty.length; j++) {
                    if (centroidy[i][2] == punkty[j][2]) {
                        iloscPunktow++;
                        x = x + punkty[j][0];
                        y = y + punkty[j][1];
                    }
                }

                if(iloscPunktow != 0) {
                    x = x / iloscPunktow;
                    y = y / iloscPunktow;
                }

                if(centroidy[i][0] == x && centroidy[i][1] == y) {
                    // jesli wspolrzedne centroidu nie zmienily sie, to oznacza
                    // ze grupa punktow ktore sa do niego przypisane rowniez sie nie zmienila
                    // z czego wynika, ze zaden punkt nie zmienil swojej grupy
                    czyZakonczony = true;
                }

                centroidy[i][0] = x;
                centroidy[i][1] = y;
            }
        }
    }
}