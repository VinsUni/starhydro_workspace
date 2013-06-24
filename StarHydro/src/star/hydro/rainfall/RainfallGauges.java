package star.hydro.rainfall;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.vecmath.Point3f;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.GridwStat;
import star.hydrology.events.RainfallGaugesRaiser;
import star.hydrology.events.interfaces.PaletteRenderableLayer;

@SignalComponent(extend = JPanel.class)
public class RainfallGauges extends RainfallGauges_generated implements ActionListener
{
    private static final long serialVersionUID = 1L;
    private DefaultListModel listModel;
    private JList list;
    private JButton saveGauges;
    private Point3f center;
    private float cellsize;
    private Collection<Point> raingauges;
    private PaletteRenderableLayer layer;

	@Override
	public void addNotify()
	{
		listModel = new DefaultListModel();
		list = new JList(listModel);
		list.setEnabled(false);
		list.setVisibleRowCount(5);
		list.setEnabled(false);
		list.setVisibleRowCount(5);
		list.setModel(listModel);

		saveGauges = new JButton("Save Rain gauges");
		saveGauges.addActionListener(this);
		super.addNotify();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(saveGauges);
		add(new JScrollPane(list));
	}

	@Handles(raises = {})
	void updateGauges(RainfallGaugesRaiser raiser)
	{
		raingauges = raiser.getGauges();
		updateModel();
	}

	@Handles(raises = {})
	void updateTerrain(star.hydrology.events.map.FilledMapLayerRaiser raiser)
	{
		PaletteRenderableLayer layer = raiser.getLayer();
		if (layer != null && layer.getDataset() instanceof GridwStat)
		{
			this.layer = layer;
			GridwStat gs = (GridwStat) layer.getDataset();
			center = gs.getCenter();
			cellsize = gs.getCellsize();
			updateModel();
		}
	}

	private void updateModel()
	{
		listModel.clear();
		if (raingauges != null)
		{
			int index = 0;
			for (Point p : raingauges)
			{
				listModel.addElement(point2string(p));
				index++;
			}
		}
		invalidate();
		doLayout();
	}

	private String point2string(Point p)
	{
		if (layer != null)
		{
			float[] array = new float[3];
			layer.getDataset().getPoint(p.x, p.y, array);
			return Arrays.toString(array);
		}
		else
		{
			return "pixel (" + p.x + " " + p.y + ")";
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		saveRainGauges();
	}

	private java.io.OutputStream saveDialog() throws FileNotFoundException
	{
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setDialogTitle("Save rain gauges");
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			java.io.File f = fc.getSelectedFile();
			if (!f.getAbsolutePath().endsWith(".xls"))
			{
				f = new java.io.File(f.getAbsolutePath() + ".xls");
			}
			return new java.io.FileOutputStream(f);
		}
		return null;
	}

	private void saveRainGauges()
	{
		try
		{
			java.io.OutputStream os = saveDialog();
			if (os != null)
			{
				HSSFWorkbook wb = new HSSFWorkbook();
				HSSFSheet sheet = wb.createSheet("rainfall");
				writeHeader(sheet, 0);
				int row = 1;
				for (Point p : raingauges)
				{
					writeRainfall(sheet, row++);
				}
				wb.write(os);
				os.close();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void writeRainfall(HSSFSheet sheet, int rindex)
	{
		HSSFRow row = sheet.createRow(rindex);
		short index = 0;
		HSSFCell cell0 = row.createCell(index);
		cell0.setCellValue(rindex - 1);
		if (raingauges != null)
		{
			int pos = 0;
			for (Point p : raingauges)
			{
				HSSFCell cell = row.createCell(++index);
				cell.setCellValue(++pos == rindex ? 1 : 0);
			}
		}
	}

	private void writeHeader(HSSFSheet sheet, int rindex)
	{
		HSSFRow row = sheet.createRow(rindex);
		short index = 0;
		HSSFCell cell0 = row.createCell(index);
		cell0.setCellValue(new HSSFRichTextString("Time (hours)"));
		if (raingauges != null)
		{
			for (Point p : raingauges)
			{
				HSSFCell cell = row.createCell(++index);
				cell.setCellValue(new HSSFRichTextString(point2string(p)));
			}
		}
	}

}
