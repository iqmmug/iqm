package at.mug.iqm.api.model;

/*
 * #%L
 * Project: IQM - API
 * File: IqmDataBox.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2019 Helmut Ahammer, Philipp Kainz
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.media.jai.PlanarImage;
import javax.swing.JTable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Resources;
import at.mug.iqm.api.gui.IThumbnailSupport;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.workflow.IManager;
import at.mug.iqm.api.workflow.ITank;
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.commons.util.image.Thumbnail;
import at.mug.iqm.commons.util.plot.PlotTools;
import at.mug.iqm.commons.util.table.TableTools;

/**
 * This class is a container for IQM's main data types.
 * <p>
 * <b>Changes</b>
 * <ul>
 * <li>2012 04 08 PK: added TYPE_UNSUPPORTED = -1 for not supported data type</li>
 * <li>2012 10 16 MMR: added TYPE_PLOT + variables/functions</li>
 * </ul>
 * 
 * @author Helmut Ahammer, Philipp Kainz, Michael Mayrhofer-R.
 * @since 2012 02 18
 * @update 2018 06 27 thumbnail creation only when they are actually needed (according to Kleinowitz).
 * 
 */
public class IqmDataBox implements Serializable, IThumbnailSupport, Cloneable {

	/**
	 * The UID for object serialization.
	 */
	private static final long serialVersionUID = 8167169441592033240L;

	/**
	 * Class specific logger.
	 */
	private transient static final Logger logger = LogManager.getLogger(IqmDataBox.class);

	/**
	 * The unique ID of this object.
	 */
	private UUID id = UUID.randomUUID();

	/**
	 * A container for custom properties of the {@link IqmDataBox}.
	 */
	protected HashMap<String, Object> properties = new HashMap<String, Object>();

	/**
	 * A cached custom sized thumb nail of the item's content.
	 */
	protected transient Thumbnail thumbnail = null;

	/**
	 * A cached fixed sized (40x40) manager thumb nail of the item's content.
	 */
	protected transient Thumbnail managerThumbnail = null;

	/**
	 * A cached custom sized (40x40) tank thumb nail of the item's content.
	 */
	protected transient Thumbnail tankThumbnail = null;

	/**
	 * The {@link DataType} of this object.
	 */
	public DataType dataType = null;

	/**
	 * The variable for the image.
	 */
	private ImageModel imageModel = null;
	/**
	 * The variable for the plot model.
	 */
	private PlotModel plotModel = null;
	/**
	 * The variable for the table model.
	 */
	private at.mug.iqm.api.model.TableModel tableModel = null;
	/**
	 * The variable for the custom content.
	 */
	private CustomDataModel customDataModel = null;
	/**
	 * The variable for the empty content, which is always <code>null</code>.
	 */
	private Object emptyContent = null;

	/**
	 * This method creates an instance of {@link IqmDataBox} containing a
	 * specified {@link ImageModel}. The data type is automatically set to
	 * {@link DataType#IMAGE}.
	 * 
	 * @param im
	 * 
	 */
	public IqmDataBox(ImageModel im) {
		this.setImageModel(im);
	}

	/**
	 * This method creates an instance of {@link IqmDataBox} containing a
	 * specified {@link PlanarImage}. The data type is automatically set to
	 * {@link DataType#IMAGE}.
	 * 
	 * @param pi
	 * 
	 */
	public IqmDataBox(PlanarImage pi) {
		this.setImage(pi);
	}

	/**
	 * This method creates an instance of {@link IqmDataBox} containing a
	 * specified table model. The data type is automatically set to
	 * {@link DataType#TABLE}.
	 * 
	 * @param tableModel
	 *            a {@link JTable}
	 * @deprecated The use of this method is discouraged, better use
	 *             {@link #setTableModel(TableModel)}
	 */
	@Deprecated
	public IqmDataBox(JTable tableModel) {
		this.setTableModel(tableModel);
	}

	/**
	 * This method creates an instance of {@link IqmDataBox} containing a
	 * specified table model. The data type is automatically set to
	 * {@link DataType#TABLE}.
	 * 
	 * @param tableModel
	 * 
	 */
	public IqmDataBox(TableModel tableModel) {
		this.setTableModel(tableModel);
	}

	/**
	 * This method creates an instance of {@link IqmDataBox} containing a
	 * specified {@link PlotModel}. The data type is automatically set to
	 * {@link DataType#PLOT}.
	 * 
	 * @param plotModel
	 * 
	 */
	public IqmDataBox(PlotModel plotModel) {
		this.setPlotModel(plotModel);
	}

	/**
	 * This method creates an instance of {@link IqmDataBox} containing a
	 * specified custom content. The data type is automatically set to
	 * {@link DataType#CUSTOM}.
	 * 
	 * @param customContent
	 * 
	 */
	public IqmDataBox(CustomDataModel customContent) {
		this.setCustomContent(customContent);
	}

	/**
	 * This method creates an instance of {@link IqmDataBox} containing an empty
	 * object. The data type is automatically set to {@link DataType#EMPTY}.
	 */
	public IqmDataBox() {
		this.setEmptyContent(emptyContent);
	}

	/**
	 * Get the {@link PlanarImage} from the image model.
	 * 
	 * @return the image
	 */
	public PlanarImage getImage() {
		return imageModel.getImage();
	}

	/**
	 * The data type is automatically set to {@link DataType#IMAGE}.
	 * 
	 * @param planarImage
	 *            the planarImage to set
	 */
	public void setImage(PlanarImage planarImage) {
		this.dataType = DataType.IMAGE;

		this.imageModel = new ImageModel(planarImage);
		this.plotModel = null;
		this.tableModel = null;
		this.customDataModel = null;
		this.emptyContent = null;

		// read all image model properties and put them to the data box
		this.properties.putAll(this.imageModel.properties);

		//this.createThumbnails();
	}

	/**
	 * @return the planarImage
	 */
	public ImageModel getImageModel() {
		return imageModel;
	}

	/**
	 * The data type is automatically set to {@link DataType#IMAGE}.
	 * 
	 * @param imageModel
	 *            the planarImage to set
	 */
	public void setImageModel(ImageModel imageModel) {
		this.dataType = DataType.IMAGE;

		this.imageModel = imageModel;
		this.plotModel = null;
		this.tableModel = null;
		this.customDataModel = null;
		this.emptyContent = null;

		// read all image model properties and put them to the data box
		this.properties.putAll(this.imageModel.properties);

		//this.createThumbnails();
	}

	/**
	 * @return the {@link TableModel}
	 */
	public TableModel getTableModel() {
		return tableModel;
	}

	/**
	 * The data type is automatically set to {@link DataType#TABLE}.
	 * <p>
	 * This method is just for downward compatibility and its use is
	 * discouraged, since data is stored in a {@link TableModel}.
	 * 
	 * @param table
	 *            the {@link JTable} to set
	 * @deprecated Use {@link #setTableModel(TableModel)} instead.
	 */
	public void setTableModel(JTable table) throws ClassCastException {
		this.dataType = DataType.TABLE;

		this.imageModel = null;
		this.plotModel = null;

		this.tableModel = TableTools.convertToTableModel(table.getModel());
		this.customDataModel = null;
		this.emptyContent = null;

		this.properties.putAll(tableModel.getProperties());

		//this.createThumbnails();
	}

	/**
	 * The data type is automatically set to {@link DataType#TABLE}.
	 * 
	 * @param tableModel
	 *            the {@link TableModel} to set
	 */
	public void setTableModel(TableModel tableModel) {
		this.dataType = DataType.TABLE;

		this.imageModel = null;
		this.plotModel = null;
		this.tableModel = tableModel;
		this.customDataModel = null;
		this.emptyContent = null;

		this.properties.putAll(tableModel.getProperties());

		//this.createThumbnails();
	}

	/**
	 * @return the plotData
	 */
	public PlotModel getPlotModel() {
		return plotModel;
	}

	/**
	 * The data type is automatically set to {@link DataType#PLOT}.
	 * 
	 * @param plotModel
	 *            the {@link PlotModel} to set
	 */
	public void setPlotModel(PlotModel plotModel) {
		this.dataType = DataType.PLOT;

		this.imageModel = null;
		this.plotModel = plotModel;
		this.tableModel = null;
		this.customDataModel = null;
		this.emptyContent = null;

		this.properties.putAll(plotModel.getProperties());

		//this.createThumbnails();
	}

	/**
	 * Get the custom content.
	 * 
	 * @return the custom content
	 */
	public CustomDataModel getCustomContent() {
		return customDataModel;
	}

	/**
	 * The data type is automatically set to {@link DataType#CUSTOM}.
	 * 
	 * @param customContent
	 */
	public void setCustomContent(CustomDataModel customContent) {
		this.dataType = DataType.CUSTOM;

		this.imageModel = null;
		this.plotModel = null;
		this.tableModel = null;
		this.customDataModel = customContent;
		this.emptyContent = null;

		//this.createThumbnails();
	}

	/**
	 * This method is just used for the annotation of the empty content. There
	 * does not exist any The data type is automatically set to
	 * {@link DataType#EMPTY}.
	 * 
	 * @param emptyContent
	 *            will always be set to <code>null</code>
	 */
	public void setEmptyContent(Object emptyContent) {
		this.dataType = DataType.EMPTY;

		this.imageModel = null;
		this.plotModel = null;
		this.tableModel = null;
		this.customDataModel = null;
		this.emptyContent = null; // this object is always null

		//this.createThumbnails();
	}

	/**
	 * @return the dataType
	 */
	public DataType getDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            the dataType to set
	 */
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	/**
	 * Gets the value for the requested property.
	 * 
	 * @param key
	 *            the key for the value
	 * @return the value, or <code>null</code> if nothing is associated with the
	 *         given key
	 */
	public Object getProperty(String key) {
		return this.properties.get(key);
	}

	/**
	 * Sets a key-value pair to the properties of this object.
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, Object value) {
		this.properties.put(key, value);
	}

	/**
	 * Get all custom properties for this {@link IqmDataBox}.
	 * 
	 * @return all properties
	 */
	public HashMap<String, Object> getProperties() {
		return properties;
	}

	/**
	 * Set the property object for this {@link IqmDataBox}.
	 * 
	 * @param properties
	 */
	public void setProperties(HashMap<String, Object> properties) {
		this.properties = properties;
	}

	/**
	 * Creates a {@link Thumbnail}.
	 * 
	 * @param width
	 * @param height
	 * @param keepAspectRatio
	 */
	public Image createThumbnail(int width, int height, boolean keepAspectRatio) {
		this.thumbnail = new Thumbnail(ImageTools.createThumbnail(
				toPlanarImage(this), width, height, keepAspectRatio, false)
				.getImage());
		return this.thumbnail.getImage();
	}

	/**
	 * Creates the {@link Thumbnail} for the {@link IManager}.
	 * 
	 * @return the thumbnail
	 */
	public Thumbnail createManagerThumbnail() {
		this.managerThumbnail = ImageTools
				.createManagerThumb(toPlanarImage(this));
		return this.managerThumbnail;
	}

	/**
	 * Creates the {@link Thumbnail} for the {@link ITank}.
	 * 
	 * @return the thumbnail
	 */
	public Thumbnail createTankThumbnail() {
		this.tankThumbnail = ImageTools.createTankThumb(toPlanarImage(this));
		return this.tankThumbnail;
	}

	/**
	 * Gets the thumbnail.
	 * 
	 * @return the custom thumbnail
	 */
	public Thumbnail getThumbnail() {
        if (thumbnail == null) {
            thumbnail = new Thumbnail(ImageTools.createThumbnail(
                    toPlanarImage(this), 110, -1, true, false)
                    .getImage());
        }
		return thumbnail;
	}

	/**
	 * Set the thumbnail.
	 * 
	 * @param thumbnail
	 */
	public void setThumbnail(Thumbnail thumbnail) {
		this.thumbnail = thumbnail;
	}

	/**
	 * Get the {@link Thumbnail} for the {@link IManager}.
	 * 
	 * @return the manager thumbnail
	 */
	public Thumbnail getManagerThumbnail() {
        if (managerThumbnail == null) {
            managerThumbnail = createManagerThumbnail();
        }
		return managerThumbnail;
	}

	/**
	 * Set the {@link Thumbnail} for the {@link IManager}.
	 * 
	 * @param managerThumbnail
	 */
	public void setManagerThumbnail(Thumbnail managerThumbnail) {
		this.managerThumbnail = managerThumbnail;
	}

	/**
	 * Get the {@link Thumbnail} for the {@link ITank}.
	 * 
	 * @return the thumbnail for the tank
	 */
	public Thumbnail getTankThumbnail() {
        if (tankThumbnail == null) {
            tankThumbnail = createTankThumbnail();
        }
		return tankThumbnail;
	}

	/**
	 * Set the {@link Thumbnail} for the {@link ITank}.
	 * 
	 * @param tankThumbnail
	 */
	public void setTankThumbnail(Thumbnail tankThumbnail) {
		this.tankThumbnail = tankThumbnail;
	}

	/**
	 * Creates all three thumb nails for the containing data model.
	 * Should not be used because it can ramp up computation time, when thumbnails aren't actually needed 
	 * e.g. Scripts or an IQM operator which uses another IQM operator
	 */
	@Deprecated
	public void createThumbnails() {
		this.createTankThumbnail();
		//// CommonTools.showImage(this.getTankThumbnail(), "Tank Thumbnail");
		this.createManagerThumbnail();
		//// CommonTools.showImage(this.getManagerThumbnail(),
		// "Manager Thumbnail");
		this.createThumbnail(110, -1, true);
		//// CommonTools.showImage(this.getThumbnail(), "Custom Thumbnail");
	}

	/**
	 * Get the data type of the object as string.
	 * 
	 * @return a string representation of the {@link DataType}
	 */
	public String getDataTypeAsString() {
		return DataType.dataTypeAsString(this.getDataType());
	}

	/**
	 * Returns the unique identifier of this {@link IqmDataBox} object.
	 * 
	 * @return the id of the object
	 */
	public UUID getID() {
		return id;
	}

	/**
	 * This method creates a {@link PlanarImage} from the content of an
	 * {@link IqmDataBox}.
	 * <p>
	 * This is a convenient method for creating images from the content before
	 * e.g. scaling the image down for a thumb nail or any other further
	 * processing.
	 * 
	 * @param iqmDataBox
	 *            the box, of whose content the image should be created
	 * @return a {@link PlanarImage} or <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the parameter is <code>null</code>
	 */
	public static PlanarImage toPlanarImage(IqmDataBox iqmDataBox) {
		if (iqmDataBox == null) {
			throw new IllegalArgumentException(
					"The passed IqmDataBox must not be null!");
		}

		PlanarImage pi = null;

		switch (iqmDataBox.getDataType()) {
		case IMAGE:
			logger.trace("Found an image in the IqmDataBox, returning image unchanged.");
			pi = iqmDataBox.getImageModel().getImage();
			break;

		case PLOT:
			logger.trace("Found a PlotModel in the IqmDataBox, producing and returning an image of the plot.");
			try {
				pi = PlotTools.toPlanarImage(iqmDataBox.getPlotModel(), false);
			} catch (Exception e1) {
				logger.error(
						"An error occurred, the plot model cannot be converted to a PlanarImage!",
						e1);
			}
			break;

		case TABLE:
			logger.trace("Found a TableModel in the IqmDataBox, producing and returning a generic icon.");
			// load the PNG thumb nail for the table
			try {
				BufferedImage bi = ImageIO.read(Resources
						.getImageURL("icon.table.generic128"));
				pi = PlanarImage.wrapRenderedImage(bi);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case CUSTOM:
			// load the PNG thumb nail for the custom type (text)
			try {
				BufferedImage bi = ImageIO.read(Resources
						.getImageURL("icon.text.generic128"));
				pi = PlanarImage.wrapRenderedImage(bi);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case EMPTY:
			// load the PNG thumb nail for the empty type
			try {
				BufferedImage bi = ImageIO.read(Resources
						.getImageURL("icon.emptyContent.generic128"));
				pi = PlanarImage.wrapRenderedImage(bi);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case UNSUPPORTED:
		default:
			// load the PNG thumb nail for the unsupported type
			try {
				BufferedImage bi = ImageIO.read(Resources
						.getImageURL("icon.unsupportedContent.generic64"));
				pi = PlanarImage.wrapRenderedImage(bi);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
		return pi;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IqmDataBox clone() throws CloneNotSupportedException {
		IqmDataBox theClone = null;
		switch (getDataType()) {
		case IMAGE:
			theClone = new IqmDataBox(getImageModel().clone());
			break;
		case PLOT:
			theClone = new IqmDataBox(getPlotModel().clone());
			break;
		case TABLE:
			theClone = new IqmDataBox(getTableModel().clone());
			break;
		case CUSTOM:
			theClone = new IqmDataBox(getCustomContent().clone());
			break;
		case EMPTY:
			theClone = new IqmDataBox();
			break;
		default:
		case UNSUPPORTED:
			break;
		}

		theClone.setProperties((HashMap<String, Object>) getProperties()
				.clone());

		return theClone;
	}

	/**
	 * Gets the data model according to the current content.
	 * 
	 * @return <ul>
	 *         <li>an {@link ImageModel}, if {@link DataType#IMAGE}
	 *         <li>a {@link PlotModel}, if {@link DataType#PLOT}
	 *         <li>a {@link TableModel}, if {@link DataType#TABLE}
	 *         <li>a {@link CustomDataModel}, if {@link DataType#CUSTOM}
	 *         </ul>
	 */
	public IDataModel getDataModel() {
		switch (this.dataType) {
		case IMAGE:
			return this.getImageModel();
		case PLOT:
			return this.getPlotModel();
		case TABLE:
			return this.getTableModel();
		case CUSTOM:
			return this.getCustomContent();
		default:
			return null;
		}
	}
}// END
