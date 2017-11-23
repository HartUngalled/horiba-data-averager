import static org.assertj.core.api.Assertions.*;

import com.ce2tech.averager.model.dto.Measurand;
import com.ce2tech.averager.presenter.XlsPresenter;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(DataProviderRunner.class)
public class XlsPresenterTests {

    private XlsPresenter presenter;

    @DataProvider
    public static String[] filePathProvider() {
        return new String[] {
                "testfile_tensecounds_no_temp.xls",
                "testfile_tensecounds_no.xls",
                "testfile_tensecounds.xls",
                "testfile_oneminute.xls",
                //Wrong files
                "some_random_workbook.xls",
                "wrong_file_format.jpg",
                ""};
    }


    @Test
    @UseDataProvider("filePathProvider")
    public void shouldConvertHeaderListToArray(String testFilePath) {
        //Given
        presenter = new XlsPresenter(testFilePath);
        List< List<Measurand> > measurement = presenter.getData().getMeasurement();

        //When
        String[] headerArray = presenter.getHeaderToDisplay();

        //Then
        for (List<Measurand> sample : measurement) {
            assertThat(headerArray.length).isEqualTo(sample.size());
        }
    }


    @Test
    @UseDataProvider("filePathProvider")
    public void shouldConvertDataListToArray(String testFilePath) {
        //Given
        presenter = new XlsPresenter(testFilePath);
        List< List<Measurand> > measurement = presenter.getData().getMeasurement();

        //When
        Object[][] dataArray = presenter.getDataToDisplay();

        //Then
        assertThat(dataArray.length).isEqualTo(measurement.size());
        for (int i=0; i<measurement.size(); i++) {
            assertThat(dataArray[i].length).isEqualTo(measurement.get(i).size());
        }
    }



}