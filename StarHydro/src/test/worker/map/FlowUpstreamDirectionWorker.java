package test.worker.map;

import star.event.Adapter;
import star.hydrology.data.layers.ByteDataset;
import star.hydrology.data.layers.ProjectedTerrainMap;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.FlowdirectionMapLayerRaiser;

public class FlowUpstreamDirectionWorker implements FlowdirectionMapLayerRaiser
{
	app.worker.map.FlowUpstramDirectionWorker worker = new app.worker.map.FlowUpstramDirectionWorker();
	ProjectedTerrainMap layer = new ProjectedTerrainMap();
	ByteDataset dataset;

	public void run()
	{
		nullTest();
		downTest();
		complexTest();
	}

	public void complexTest()
	{
		System.out.println("nullTest");
		dataset = new ByteDataset();
		dataset.setCellsize(30);
		dataset.setSize(4, 4);
		int row = 0;
		int col = 0;
		dataset.setElementAt(col++, row, 1);
		dataset.setElementAt(col++, row, 2);
		dataset.setElementAt(col++, row, 16);
		dataset.setElementAt(col++, row, 8);
		row++;
		col = 0;
		dataset.setElementAt(col++, row, 128);
		dataset.setElementAt(col++, row, 4);
		dataset.setElementAt(col++, row, 4);
		dataset.setElementAt(col++, row, 4);
		row++;
		col = 0;
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 8);
		dataset.setElementAt(col++, row, 8);
		dataset.setElementAt(col++, row, 16);
		row++;
		col = 0;
		dataset.setElementAt(col++, row, 64);
		dataset.setElementAt(col++, row, 16);
		dataset.setElementAt(col++, row, 64);
		dataset.setElementAt(col++, row, 32);
		row++;

		layer.setDataset(dataset);
		worker.calculate(this);
		ByteDataset ret = (ByteDataset) worker.getLayer().getDataset();
		for (int y = 0; y < ret.getRows(); y++)
		{
			for (int x = 0; x < ret.getCols(); x++)
			{
				System.out.print(" " + Integer.toString((int) ret.get(x, y), 2));
			}
			System.out.println();
		}
		for (int i = 1; i <= 128; i *= 2)
		{
			System.out.println(i + " " + ((byte) ret.get(1, 0) & i));
		}

	}

	public void downTest()
	{
		System.out.println("nullTest");
		dataset = new ByteDataset();
		dataset.setCellsize(30);
		dataset.setSize(4, 4);
		int row = 0;
		int col = 0;
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 4);
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		row++;
		col = 0;
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 4);
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		row++;
		col = 0;
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 4);
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		row++;
		col = 0;
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		row++;

		layer.setDataset(dataset);
		worker.calculate(this);
		ByteDataset ret = (ByteDataset) worker.getLayer().getDataset();
		for (int y = 0; y < ret.getRows(); y++)
		{

			for (int x = 0; x < ret.getCols(); x++)
			{

				if (x == 1 && y >= 1)
				{
					if (ret.get(x, y) != 4)
					{
						throw new RuntimeException("Expect 4");
					}
				}
				else
				{
					if (ret.get(x, y) != 0)
					{
						throw new RuntimeException("Expect 0 " + x + " " + y);
					}

				}

			}
		}
	}

	public void nullTest()
	{
		System.out.println("nullTest");
		dataset = new ByteDataset();
		dataset.setCellsize(30);
		dataset.setSize(4, 4);
		int row = 0;
		int col = 0;
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		row++;
		col = 0;
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		row++;
		col = 0;
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		row++;
		col = 0;
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		dataset.setElementAt(col++, row, 0);
		row++;

		layer.setDataset(dataset);
		worker.calculate(this);
		ByteDataset ret = (ByteDataset) worker.getLayer().getDataset();
		for (int y = 0; y < ret.getRows(); y++)
		{
			for (int x = 0; x < ret.getCols(); x++)
			{
				if (ret.get(x, y) != 0)
				{
					throw new RuntimeException("Expect 0");
				}
			}
		}
	}

	public static void main(String[] args)
	{
		FlowUpstreamDirectionWorker test = new FlowUpstreamDirectionWorker();
		test.run();
	}

	public void addNotify()
	{
	}

	public Adapter getAdapter()
	{
		return null;
	}

	public void removeNotify()
	{
	}

	public PaletteRenderableLayer getLayer()
	{
		return layer;
	}

	public int getLayerKind()
	{
		return 0;
	}

}
