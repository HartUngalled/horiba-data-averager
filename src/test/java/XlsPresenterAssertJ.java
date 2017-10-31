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

    private Object invokePrivateMethod(String methodName) {
        try {
            Method method = XlsPresenter.class.getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(presenter);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

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
    public void shouldConvertHeaderListToArray(String testFilePath) {
        //Given
        presenter = new XlsPresenter(testFilePath);
        List<String> dtoHeaderList = presenter.getDto().getDataHeader();

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
        List<List<Object>> dtoDataList = presenter.getDto().getDataColumns();

        int dtoRowSize = dtoDataList.size();
        int dtoColumnSize = dtoDataList.get(0).size();

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
        int samplingTime = (int) invokePrivateMethod("getDataSamplingTime");

        //Then
        if ( testFilePath.contains("tensecounds") ) {
            assertThat(samplingTime).isEqualTo(10);
        } else {
            assertThat(samplingTime).isEqualTo(60);
        }
    }


}
