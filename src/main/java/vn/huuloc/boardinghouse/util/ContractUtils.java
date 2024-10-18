package vn.huuloc.boardinghouse.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import vn.huuloc.boardinghouse.constant.SettingConstants;
import vn.huuloc.boardinghouse.entity.Branch;
import vn.huuloc.boardinghouse.entity.Contract;
import vn.huuloc.boardinghouse.entity.ContractCustomerLinked;
import vn.huuloc.boardinghouse.entity.Customer;
import vn.huuloc.boardinghouse.repository.ContractCustomerLinkedRepository;
import vn.huuloc.boardinghouse.service.SettingService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@UtilityClass
public class ContractUtils {
    public static Map<String, String> mapContractToPlaceholders(Contract contract, SettingService settingService,
                                                                ContractCustomerLinkedRepository contractCustomerLinkedRepository) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'ngày' dd 'tháng' MM 'năm' yyyy");
        Map<String, String> data = new HashMap<>();
        Branch branch = contract.getRoom().getBranch();
        double electricUnitPrice = Double.parseDouble(settingService.getSetting(SettingConstants.ELECTRICITY_UNIT_PRICE));

        // lessor information
        data.put("lessor_name", StringUtils.upperCase(branch.getLessorName()));
        data.put("lessor_birth", branch.getLessorBirth());
        data.put("lessor_cccd", branch.getLessorCCCD());
        data.put("ngay_cap", branch.getNgayCap());
        data.put("noi_cap", branch.getNoiCap());
        data.put("lessor_address", branch.getLessorAddress());
        data.put("lessor_phone", branch.getLessorPhone());
        data.put("branch_name", branch.getName());
        data.put("branch_address", branch.getAddress());

        // print day
        data.put("print_day", LocalDate.now().format(formatter));

        // Map room and branch information
        data.put("room_name", contract.getRoom().getName());
        data.put("room_address", contract.getRoom().getBranch().getAddress());

        // Map contract details
        data.put("start_date", contract.getStartDate().format(formatter));

        Period period = Period.between(contract.getStartDate(), contract.getEndDate() != null ? contract.getEndDate() : LocalDate.now());
        int year = period.getYears();
        year = year == 0 ? 1 : year;
        data.put("period", String.valueOf(year));
        data.put("period_month", String.valueOf(year * 12));

        data.put("price", formatCurrency(contract.getPrice()));
        data.put("tien_chu", CurrencyUtils.getTienChu(contract.getPrice()));
        data.put("electric_unit_price", formatCurrency(BigDecimal.valueOf(electricUnitPrice)));
        data.put("checkin_electric_number", String.valueOf(contract.getCheckinElectricNumber()));
//        data.put("deposit", formatCurrency(contract.getDeposit()));
//        data.put("note", contract.getNote() != null ? contract.getNote() : "Không có");

        // find owner
        ContractCustomerLinked primaryCustomer = contractCustomerLinkedRepository.findOwnerByContractId(contract.getId());
        Customer owner = primaryCustomer.getCustomer();
        data.put("owner_name", StringUtils.upperCase(owner.getName()));
        data.put("owner_birth", owner.getBirthday());
        data.put("owner_cccd", owner.getIdNumber());
        data.put("owner_ngay_cap", owner.getIdDate());
        data.put("owner_noi_cap", owner.getIdPlace());
        data.put("owner_address", owner.getAddress());

        return data;
    }

    private static String formatCurrency(BigDecimal value) {
        if (value == null) return "0";

        // Configure the decimal format to use dot as a grouping separator
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');  // Set '.' as thousands separator
        symbols.setDecimalSeparator(',');

        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(value);
    }
}
