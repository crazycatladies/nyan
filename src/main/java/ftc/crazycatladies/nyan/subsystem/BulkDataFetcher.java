package ftc.crazycatladies.nyan.subsystem;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.lynx.LynxModuleIntf;
import com.qualcomm.hardware.lynx.LynxNackException;
import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulkDataFetcher {
    private List<LynxModule> expansionHubs;

    public BulkDataFetcher(List<LynxModule> expansionHubs) {
        this.expansionHubs = expansionHubs;
    }

    public Map<Integer, LynxGetBulkInputDataResponse> getBulkDataResponse() throws InterruptedException {
        HashMap<Integer, LynxGetBulkInputDataResponse> bulkDataResponse = new HashMap<Integer, LynxGetBulkInputDataResponse>();
        try {
            for (LynxModule hub : expansionHubs) {
                LynxGetBulkInputDataCommand bulkCommand = new LynxGetBulkInputDataCommand((LynxModuleIntf) hub);
                LynxGetBulkInputDataResponse dataResponse = bulkCommand.sendReceive();
                bulkDataResponse.put(((LynxModuleIntf) hub).getModuleAddress(), dataResponse);
            }
        } catch (LynxNackException e) {
            e.printStackTrace();
        }
        return bulkDataResponse;
    }
}
