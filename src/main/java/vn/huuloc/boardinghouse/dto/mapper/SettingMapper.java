package vn.huuloc.boardinghouse.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.huuloc.boardinghouse.dto.SettingDto;
import vn.huuloc.boardinghouse.entity.settings.Setting;

import java.util.List;

@Mapper
public interface SettingMapper {
    SettingMapper INSTANCE = Mappers.getMapper(SettingMapper.class);

    SettingDto toDto(Setting setting);

    Setting toEntity(SettingDto dto);

    List<SettingDto> entityListDto2List(List<Setting> entity);
}
