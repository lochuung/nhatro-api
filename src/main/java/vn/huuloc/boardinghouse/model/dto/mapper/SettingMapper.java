package vn.huuloc.boardinghouse.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.huuloc.boardinghouse.model.dto.SettingDto;
import vn.huuloc.boardinghouse.model.entity.settings.Setting;

import java.util.List;

@Mapper
public interface SettingMapper {
    SettingMapper INSTANCE = Mappers.getMapper(SettingMapper.class);

    SettingDto toDto(Setting setting);

    Setting toEntity(SettingDto dto);

    List<SettingDto> entityListDto2List(List<Setting> entity);
}
