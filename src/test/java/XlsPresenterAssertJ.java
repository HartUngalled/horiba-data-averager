import static com.ce2tech.averager.myutils.TestingUtil.invokePrivateMethod;
import static org.assertj.core.api.Assertions.*;

import com.ce2tech.averager.presenter.XlsPresenter;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@RunWith(DataProviderRunner.class)
public class XlsPresenterAssertJ {

    private XlsPresenter presenter;

    @DataProvider
    public static String[] filePathProvider() {
        return new String[]
                {"testfile_tensecounds_no_temp.xls",
                        "testfile_tensecounds_no.xls",
                        "testfile_tensecounds.xls",
                        "testfile_oneminute.xls",
                        "wrong-file-name.jpg",
                        ""};
    }

    @Test
    @UseDataProvider("filePathProvider")
    public void shouldConvertHeaderListToArray(String testFilePath) {
        //Given
        presenter = new XlsPresenter(testFilePath);
        List<String> dtoHeaderList = presenter.getData().getDataHeader();

        //When
        String[] headerArray = presenter.getHeaderArray();

        //Then
        assertThat(headerArray.length).isEqualTo(dtoHeaderList.size());
    }

    @Test
    @UseDataProvider("filePathProvider")
    public void shouldConvertDataListToArray(String testFilePath) {
        //Given
        presenter = new XlsPresenter(testFilePath);
        List<List<Object>> dtoDataList = presenter.getData().getDataColumns();
        int dtoColumnSize = dtoDataList.isEmpty() ? 0 : dtoDataList.get(0).size();
        int dtoRowSize = dtoDataList.size();

        //When
        Object[][] dataArray = presenter.getDataArray();

        //Then
        assertThat(dataArray.length).isEqualTo(dtoColumnSize);
        for (int i=0; i<dataArray.length; i++) {
            assertThat(dataArray[i].length).isEqualTo(dtoRowSize);
        }

    }

    @Test
    @UseDataProvider("filePathProvider")
    public void shouldReturnSamplingTime(String testFilePath) {
        //Given
        presenter = new XlsPresenter(testFilePath);

        //When
        int samplingTime = (int) invokePrivateMethod("getDataSamplingTime", presenter);

        //Then
        if ( testFilePath.contains("tensecounds") ) {
            assertThat(samplingTime).isEqualTo(10);
        } else if ( testFilePath.contains("oneminute") ) {
            assertThat(samplingTime).isEqualTo(60);
        } else {
            assertThat(samplingTime).isEqualTo(-1);
        }

    }

    @Test
    @UseDataProvider("filePathProvider")
    public void createAveragedFile(String testFilePath) {
        //Given
        presenter = new XlsPresenter(testFilePath);
        Object[][] dataArray = presenter.getDataArray();

        //When
        presenter.getData();
        presenter.averageToOneMin();
        presenter.setData("newFile.xls");
        XlsPresenter presenterNewFile = new XlsPresenter("newFile.xls");
        Object[][] averagedDataArray = presenterNewFile.getDataArray();

        //Then
        for (int i=0; i<averagedDataArray.length; i++) {
            assertThat(averagedDataArray[i].length).isEqualTo(dataArray[i/6].length);
        }
    }

}