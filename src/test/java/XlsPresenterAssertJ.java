import static org.assertj.core.api.Assertions.*;

import com.ce2tech.averager.presenter.XlsPresenter;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class XlsPresenterAssertJ {

    private XlsPresenter presenter;

    @DataProvider
    public static String[] filePathProvider() {
        return new String[]
                {"testfile_tensecounds_no_temp.xls",
                "testfile_tensecounds_no.xls",
                "testfile_tensecounds.xls",
                "testfile_oneminute.xls"};
    }

    @Test
    @UseDataProvider("filePathProvider")
    public void shouldReturnLocalTimeIndex(String testFilePath) {
        //Given
        presenter = new XlsPresenter(testFilePath);

        //When
        int localTimeIndex = presenter.getFileLocalTimeIndex();

        //Then
        assertThat(localTimeIndex).isEqualTo(1);
    }

    @Test
    @UseDataProvider("filePathProvider")
    public void shouldReturnSamplingTime(String testFilePath) {
        //Given
        presenter = new XlsPresenter(testFilePath);

        //When
        int samplingTime = presenter.getDataSamplingTime();

        //Then
        if ( testFilePath.contains("tensecounds") ) {
            assertThat(samplingTime).isEqualTo(10);
        } else {
            assertThat(samplingTime).isEqualTo(60);
        }
    }



}
