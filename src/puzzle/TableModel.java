package puzzle;

import javax.swing.table.AbstractTableModel;
import java.util.HashSet;
import java.util.Set;

import static puzzle.Board.LENGTH;
import static puzzle.Board.SEPARATOR;

class TableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;

	private Object[][] data = new Object[LENGTH][LENGTH];  /* Data that will go inside the cells */
    private final Set<String> modifiableCells = new HashSet<>();

	
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return LENGTH;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return LENGTH;
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return data[arg0][arg1];
	}

	@Override
	 public boolean isCellEditable(int row, int col) {
		 final String celStr = row + SEPARATOR + col;
		 return modifiableCells.contains(celStr);
	 }
	 
	 public void setValueAt(Object value, int row, int col) {
	    if (row == 0 && col == 0) {
	        modifiableCells.clear();
        }
		 data[row][col] = value;
		 fireTableCellUpdated(row, col);

		 if (value instanceof String) {
		     //only allow empty cells to editable
		     if ("".equals(((String) value).trim())) {
		         modifiableCells.add(row + SEPARATOR + col);
             }
         }

	 }
}
