package warehouseClient;

import java.awt.Color;

import ail.DefaultCellRenderer;

public class NotificationDescriptionRenderer extends DefaultCellRenderer {
	protected void setValue(Object value) {
		if(value instanceof NotificationDescription) {
			NotificationDescription description = (NotificationDescription)value;
			setText(description.description);
			if(description.isNew)
				setForeground(new Color(0x0db400));
		}
		else
			super.setValue(value);
	}
}
