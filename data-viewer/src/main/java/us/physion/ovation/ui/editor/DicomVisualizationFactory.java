package us.physion.ovation.ui.editor;

import org.openide.util.lookup.ServiceProvider;
import us.physion.ovation.domain.mixin.Content;


@ServiceProvider(service = VisualizationFactory.class)
public class DicomVisualizationFactory implements VisualizationFactory{

    @Override
    public int getPreferenceForDataContentType(String contentType) {
        if (contentType.equals("application/dicom"))
        {
            return 100;
        }
        return -1;
    }

    @Override
    public DataVisualization createVisualization(Content r) {
        return new DicomWrapper(r);
    }

}
