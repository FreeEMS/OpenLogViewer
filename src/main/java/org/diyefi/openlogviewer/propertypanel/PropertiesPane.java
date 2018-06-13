/* OpenLogViewer
 *
 * Copyright 2011
 *
 * This file is part of the OpenLogViewer project.
 *
 * OpenLogViewer software is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenLogViewer software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with any OpenLogViewer software.  If not, see http://www.gnu.org/licenses/
 *
 * I ask that if you make any changes to this file you fork the code on github.com!
 *
 */
package org.diyefi.openlogviewer.propertypanel;

import javafx.scene.paint.Color;
import org.apache.commons.lang3.StringUtils;
import org.diyefi.openlogviewer.Keys;
import org.diyefi.openlogviewer.OpenLogViewer;
import org.diyefi.openlogviewer.Text;
import org.diyefi.openlogviewer.utils.JavaFXUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.*;
import java.util.List;

public class PropertiesPane extends JFrame {
	private static final long serialVersionUID = 1L;

	private final JPanel propertyView;
	private final ResourceBundle labels;
	private final String settingsDirectory;

	private File OLVProperties;
	private List<SingleProperty> properties;
	private List<SingleProperty> removeProperties;

	public PropertiesPane(final ResourceBundle labels, final String settingsDirectory) {
		super(labels.getString(Text.VIEW_MENU_ITEM_SCALE_AND_COLOR_NAME));

		this.labels = labels;
		this.settingsDirectory = settingsDirectory;

		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setPreferredSize(new Dimension(350, 500));
		setSize(new Dimension(550, 500));
		setJMenuBar(createMenuBar());

		final JPanel propertyPanel = new JPanel();
		propertyPanel.setLayout(new BorderLayout());
		OpenLogViewer.setupWindowKeyBindings(this);

		propertyView = new JPanel();
		propertyView.setPreferredSize(new Dimension(400, 0));
		propertyView.setLayout(new FlowLayout(FlowLayout.LEFT));

		final JScrollPane jsp = new JScrollPane(propertyView);
		propertyPanel.add(jsp, BorderLayout.CENTER);
		propertyPanel.add(createAcceptPanel(), BorderLayout.SOUTH);
		add(propertyPanel);
	}

	public final void setProperties(final List<SingleProperty> p) {
		removeProperties = new ArrayList<SingleProperty>();
		properties = p;
		setupForLoad();
	}

	private void setupForLoad() {
		try {
			final String systemDelim = File.separator;
			final File homeDir = new File(System.getProperty(Keys.USER_HOME));

			if (!homeDir.exists() || !homeDir.canRead() || !homeDir.canWrite()) {
				System.out.println(labels.getString(Text.HOME_DIRECTORY_NOT_ACCESSIBLE));

			} else {
				OLVProperties = new File(homeDir.getAbsolutePath() + systemDelim + settingsDirectory);
			}

			if (!OLVProperties.exists()) {
				try {
					if (OLVProperties.mkdir()) {
						OLVProperties = new File(homeDir.getAbsolutePath() + systemDelim + settingsDirectory + systemDelim + "OLVProperties.olv");
						if (OLVProperties.createNewFile()) {
							loadProperties();
						}
					} else {
						throw new RuntimeException(labels.getString(Text.FAILED_TO_CREATE_DIRECTORY_MESSAGE));
					}
				} catch (IOException ioe) {
					System.out.print(ioe.getMessage());
				}
			} else {
				OLVProperties = new File(homeDir.getAbsolutePath() + systemDelim + settingsDirectory + systemDelim + "OLVProperties.olv");
				OLVProperties.createNewFile(); // Just in case the file does not exist yet. This won't overwrite an existing file.
				loadProperties();
			}
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}

	private JMenuBar createMenuBar() {
		final JMenuBar propMenuBar = new JMenuBar();
		final JMenu options = new JMenu(labels.getString(Text.OPTIONS_MENU_NAME));
		final JMenuItem addProperty = new JMenuItem(labels.getString(Text.OPTIONS_MENU_ITEM_ADD_PROPERTY_NAME));
		final JMenuItem removeProperty = new JMenuItem(labels.getString(Text.OPTIONS_MENU_ITEM_REMOVE_PROPERTIES_NAME));

		propMenuBar.add(options);

		addProperty.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				final String s = (String) JOptionPane.showInputDialog(rootPane, labels.getString(Text.ENTER_HEADER_FOR_PROPERTY));
				if (StringUtils.isNotBlank(s)) {
					final SingleProperty newprop = new SingleProperty();
					newprop.setHeader(s);
					addProperty(newprop);
				}
			}
		});

		removeProperty.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent evt) {
				removePropertyPanels();
			}
		});

		options.add(addProperty);
		options.add(removeProperty);

		return propMenuBar;
	}

	private JPanel createAcceptPanel() {
		final JPanel aPanel = new JPanel();
		aPanel.setPreferredSize(new Dimension(500, 32));
		aPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 2));

		final JButton ok = new JButton(labels.getString(Text.OK_BUTTON));
		final JButton cancel = new JButton(labels.getString(Text.CANCEL_BUTTON));

		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				OpenLogViewer.getInstance().getPropertyPane().save();
				OpenLogViewer.getInstance().getPropertyPane().setVisible(false);
			}
		});

		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				OpenLogViewer.getInstance().getPropertyPane().resetProperties();
				OpenLogViewer.getInstance().getPropertyPane().setVisible(false);
			}
		});

		aPanel.add(cancel);
		aPanel.add(ok);

		return aPanel;
	}

	private void loadProperties() {
		try {
			final Scanner scan = new Scanner(new FileReader(OLVProperties));

			while (scan.hasNext()) {
				final String[] propLine = scan.nextLine().split("=");
				final SingleProperty sp = new SingleProperty();
				final String[] prop = propLine[1].split(",");
				sp.setHeader(propLine[0]);
				sp.setColor(new Color(
						Integer.parseInt(prop[0]),
						Integer.parseInt(prop[1]),
						Integer.parseInt(prop[2]),
						1));
				sp.setMin(Double.parseDouble(prop[3]));
				sp.setMax(Double.parseDouble(prop[4]));
				sp.setTrackIndex(Integer.parseInt(prop[5]));
				sp.setActive(Boolean.parseBoolean(prop[6]));
				addProperty(sp);
			}

			scan.close();
		} catch (FileNotFoundException fnf) {
			System.out.print(fnf.toString());
			throw new RuntimeException(fnf);
		}
	}

	public final void save() {
		try {
			removeProperties.clear();
			updateProperties();
			final FileWriter fstream = new FileWriter(OLVProperties);
			final BufferedWriter out = new BufferedWriter(fstream);

			for (int i = 0; i < properties.size(); i++) {
				out.write(properties.get(i).toString());
				out.newLine();
			}

			out.close();
		} catch (IOException e) {
			System.err.println(labels.getString(Text.ERROR) + e.getMessage());
			throw new RuntimeException(labels.getString(Text.IO_ISSUE_SAVING_PROPERTY), e);
		}
	}

	private void updateProperties() {
		for (int i = 0; i < propertyView.getComponentCount(); i++) {
			final PropertyPanel pp = (PropertyPanel) propertyView.getComponent(i);
			pp.updateSP();
		}
	}

	public final void resetProperties() {
		for (int i = 0; i < propertyView.getComponentCount(); i++) {
			final PropertyPanel pp = (PropertyPanel) propertyView.getComponent(i);
			pp.reset();
		}
		if (!removeProperties.isEmpty()) {
			for (int i = 0; i < removeProperties.size(); i++) {
				addProperty(removeProperties.get(i));
			}
			removeProperties.clear();
		}
	}

	private PropertyPanel exists(final SingleProperty sp) {

		for (int i = 0; i < propertyView.getComponentCount(); i++) {
			final PropertyPanel pp = (PropertyPanel) propertyView.getComponent(i);
			if (pp.getSp().getHeader().equalsIgnoreCase(sp.getHeader())) {
				return pp;
			}
		}
		return null;
	}

	public final void addProperty(final SingleProperty sp) {
		final PropertyPanel pp = exists(sp);
		if (pp == null) {
			properties.add(sp);
			Collections.sort(properties);
			propertyView.add(new PropertyPanel(sp), properties.indexOf(sp));
			propertyView.setPreferredSize(new Dimension(propertyView.getPreferredSize().width, propertyView.getPreferredSize().height + 60));
			propertyView.revalidate();
		} else {
			for (int i = 0; i < properties.size(); i++) {
				if (properties.get(i).getHeader().equalsIgnoreCase(sp.getHeader())) {
					properties.set(i, sp);
				}
			}
			pp.setSp(sp);
			pp.reset();
		}
	}

	public final void addPropertyAndSave(final SingleProperty sp) {
		addProperty(sp);
		save();
	}

	private void removeProperty(final SingleProperty sp) {
		if (properties.contains(sp)) {
			properties.remove(sp);
		}
	}

	private void removePropertyPanels() {
		int componentIndex = 0;
		while (componentIndex < propertyView.getComponentCount()) {
			final PropertyPanel pp = (PropertyPanel) propertyView.getComponent(componentIndex);
			if (pp.getCheck().isSelected()) {
				if (!removeProperties.contains(pp.getSp())) {
					removeProperties.add(pp.getSp());
				}

				removeProperty(pp.getSp()); // Move this to add to a queue of things to remove, in case of cancel
				propertyView.remove(propertyView.getComponent(componentIndex));
				propertyView.setPreferredSize(new Dimension(propertyView.getPreferredSize().width, propertyView.getPreferredSize().height - 60));
				propertyView.revalidate();
			} else {
				componentIndex++;
			}
		}
		propertyView.repaint();
	}

	private final class PropertyPanel extends JPanel implements Comparable<PropertyPanel> {
		private static final long serialVersionUID = 1L;
		private SingleProperty sp;
		private final JCheckBox check;
		private final JPanel colorBox;
		private final JTextField minBox;
		private final JTextField maxBox;
		private final JTextField trackBox;
		private final JComboBox activeBox;

		public PropertyPanel(final SingleProperty sp) {
			this.sp = sp;
			setLayout(new FlowLayout(FlowLayout.LEFT, 10, 2));
			setBorder(BorderFactory.createTitledBorder(sp.getHeader()));
			setPreferredSize(new Dimension(500, 50));
			final JLabel minLabel = new JLabel(labels.getString(Text.MIN_PROPERTY));
			final JLabel maxLabel = new JLabel(labels.getString(Text.MAX_PROPERTY));
			final JLabel colorLabel = new JLabel(labels.getString(Text.COLOR_PROPERTY));
			final JLabel splitLabel = new JLabel(labels.getString(Text.SPLIT_PROPERTY));
			final JLabel activeLabel = new JLabel(labels.getString(Text.ACTIVE_PROPERTY));
			trackBox = new JTextField();
			trackBox.setPreferredSize(new Dimension(15, 20));
			trackBox.setText(Integer.toString(sp.getTrackIndex()));
			minBox = new JTextField();
			minBox.setPreferredSize(new Dimension(50, 20));
			minBox.setText(Double.toString(sp.getMin()));
			maxBox = new JTextField();
			maxBox.setPreferredSize(new Dimension(50, 20));
			maxBox.setText(Double.toString(sp.getMax()));
			colorBox = new JPanel();
			colorBox.setBackground(JavaFXUtils.convertFXColorToAWTColor(sp.getColor()));
			colorBox.setPreferredSize(new Dimension(30, 20));
			final String[] tf = {labels.getString(Text.TRUE), labels.getString(Text.FALSE)};
			activeBox = new JComboBox(tf);

			if (sp.isActive()) {
				activeBox.setSelectedIndex(1);
			}

			activeBox.setPreferredSize(new Dimension(60, 20));
			check = new JCheckBox();

			colorBox.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(final MouseEvent e) {
					final java.awt.Color newColor = JColorChooser.showDialog(OpenLogViewer.getInstance().getOptionFrame(), labels.getString(Text.CHOOSE_NEW_COLOR), colorBox.getBackground());
					if (newColor != null) {
						colorBox.setBackground(newColor);
					}
				}

				@Override
				public void mouseClicked(final MouseEvent e) {
				}

				@Override
				public void mouseEntered(final MouseEvent e) {
				}

				@Override
				public void mouseExited(final MouseEvent e) {
				}

				@Override
				public void mousePressed(final MouseEvent e) {
				}
			});

			add(colorLabel);
			add(colorBox);
			add(minLabel);
			add(minBox);
			add(maxLabel);
			add(maxBox);
			add(splitLabel);
			add(trackBox);
			add(activeLabel);
			add(activeBox);
			add(check);
		}

		public JCheckBox getCheck() {
			return check;
		}

		public SingleProperty getSp() {
			return sp;
		}

		public void setSp(final SingleProperty sp) {
			this.sp = sp;
		}

		public void updateSP() {
			sp.setMin(Double.parseDouble(minBox.getText()));
			sp.setMax(Double.parseDouble(maxBox.getText()));
			sp.setColor(JavaFXUtils.convertAWTColorToFXColor(colorBox.getBackground()));
			sp.setTrackIndex(Integer.parseInt(trackBox.getText()));
			final String active = (String) activeBox.getSelectedItem();
			sp.setActive(Boolean.parseBoolean(active));
		}

		public void reset() {
			minBox.setText(Double.toString(sp.getMin()));
			maxBox.setText(Double.toString(sp.getMax()));
			colorBox.setBackground(JavaFXUtils.convertFXColorToAWTColor(sp.getColor()));
			trackBox.setText(Integer.toString(sp.getTrackIndex()));
			activeBox.setSelectedItem(Boolean.toString(sp.isActive()));
		}

		@Override
		public int compareTo(final PropertyPanel pp) {
			return this.sp.getHeader().compareToIgnoreCase(pp.getSp().getHeader());
		}
	}
}
