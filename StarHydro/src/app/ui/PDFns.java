package app.ui;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import star.annotations.SignalComponent;
import star.hydrology.ui.chart.pdf.CurvaturePDFChart;
import star.hydrology.ui.chart.pdf.ElevationPDFChart;
import star.hydrology.ui.chart.pdf.SlopePDFChart;
import star.hydrology.ui.chart.pdf.TopIndexPDFChart;

@SignalComponent(extend = JPanel.class)
public class PDFns extends PDFns_generated
{
	private static final long serialVersionUID = 1L;

	public void addNotify()
	{
		super.addNotify();
		setLayout(new FlowLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(new ElevationPDFChart());
		panel.add(new SlopePDFChart());
		panel.add(new CurvaturePDFChart());
		panel.add(new TopIndexPDFChart());
		add(panel);

	}

}
