package puzzle;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

import static puzzle.Board.LENGTH;


public class GuiComponent extends JPanel implements ActionListener{
    Board board;

	static JFrame frame;
	static JTable table;

	static JButton generateSudoku;
	static JButton loadFromFile;
	static JButton solveButton;
	static JButton checkAnswer;

	static JTextArea textArea;

	public GuiComponent() throws Exception {
		super();
		board = null;

        generateSudoku = new JButton("generate new puzzle");
        generateSudoku.addActionListener(this);

        loadFromFile = new JButton("load from file");
        loadFromFile.addActionListener(this);

        checkAnswer = new JButton("check");
        checkAnswer.addActionListener(this);

        solveButton = new JButton("solve");
        solveButton.addActionListener(this);

        textArea = new JTextArea("info panel");
        textArea.setEditable(false);
        textArea.setSize(new Dimension(300, 300));
	}
	
	
	public static void displayFrame() throws Exception{
		frame = new JFrame("Sudoku sudoku.puzzle.Solver");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final GuiComponent comp = new GuiComponent();
		frame.setContentPane(comp);

		frame.add(generateSudoku);
		frame.add(loadFromFile);
		frame.add(checkAnswer);
		frame.add(solveButton);

		frame.add(textArea);

		frame.setResizable(false);
		
		table = new JTable(new TableModel()){
		    public Component prepareRenderer (TableCellRenderer renderer, int index_row, int index_col){
		    	
		        Component comp = super.prepareRenderer(renderer, index_row, index_col);  
		        if(((index_row < 3 || index_row > 5) &&
						(index_col < 3 || index_col > 5)) ||
                        (index_col > 2 && index_col < 6 &&
                         index_row > 2 && index_row < 6) ){
		             comp.setBackground(new Color(204,255,204));
		         }else{
		        	 comp.setBackground(Color.white);
		         }
		       return comp;  
		    }  			
		};
		for(int i = 0; i < 9; i++){
			DefaultTableCellRenderer alligner = new DefaultTableCellRenderer();
			alligner.setHorizontalAlignment(JLabel.CENTER);
			table.getColumnModel().getColumn(i).setCellRenderer(alligner);
		}
		table.setPreferredScrollableViewportSize(new Dimension(400, 300));
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(200, 168));
		frame.add(scrollPane);
		frame.setSize(650, 400);
		frame.setVisible(true);

	}

    void updateTable(Character[][] boardStr){
        IntStream.range(0, LENGTH)
                .forEach(i -> IntStream.range(0, LENGTH)
                        .forEach(j -> table.getModel().setValueAt("" + boardStr[i][j], i, j)));
    }

    void updateBoard() {
        final List<String> emptyCells = board.getEmptyCells();
        emptyCells.forEach(emptyCell -> {
            final String[] rowCol = emptyCell.split(Board.SEPARATOR);
            final int row = Integer.parseInt(rowCol[0]);
            final int col = Integer.parseInt(rowCol[1]);
            final String tableContent = ((String)table.getModel().getValueAt(row, col)).trim();
            final Character entry = "".equals(tableContent) ? ' ' : tableContent.charAt(0);
            final boolean isValid = board.insert(entry, row, col);
            if (!isValid) {
                return;
            }
        });
    }
	@Override
	public void actionPerformed(ActionEvent evt) {

        if (evt.getActionCommand().toLowerCase().startsWith("generate")) {
            try {
                board = Board.createBoard(false);
                updateTable(board.getTwoDBoard());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
            return;
        }

        if (evt.getActionCommand().toLowerCase().startsWith("load")) {
            try {
                final JFileChooser fileChooser = new JFileChooser(".");
                fileChooser.setFileFilter(getAcceptableFile());
                fileChooser.showDialog(null, "select");
                final File f = fileChooser.getSelectedFile();
                if (f != null) {
                    Path p = f.toPath();
                    List<String> lines = Files.readAllLines(p, Charset.defaultCharset());
                    Board tempBoard = Board.convertToBoard(lines);
                    board = tempBoard;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (board == null) {
                return;
            }
            updateTable(board.getTwoDBoard());
        }

        if(evt.getActionCommand().toLowerCase().startsWith("check")){
            if (board == null) {
                return;
            }
            updateBoard();
            final boolean isSolved = board.isSolved();

            if (isSolved) {
                textArea.setText("SOLVED!");
            } else {
                textArea.setText(":( NOT SOLVED");
            }
            return;
        }


		if(evt.getActionCommand().toLowerCase().startsWith("solve")){
            if (board == null) {
                return;
            }
            final Double start = Double.parseDouble(System.currentTimeMillis() + "");
            final Solver solver = new Solver();
            solver.solve(board);
            if (board.isSolved()) {
                updateTable(board.getTwoDBoard());
                final Double end = Double.parseDouble(System.currentTimeMillis() + "");
                final Double duration = (end - start) / 1000D;
                textArea.setText("solved in " + duration + " seconds");
            } else {
                textArea.setText("couldn't solve");
            }
		}

	}

	private FileFilter getAcceptableFile() {
        FileFilter retValue =  new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getAbsolutePath().toLowerCase().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "text file input";
            }
        };
        return retValue;
    }
	public void print(String str){
		System.out.println(str);
	}
}
