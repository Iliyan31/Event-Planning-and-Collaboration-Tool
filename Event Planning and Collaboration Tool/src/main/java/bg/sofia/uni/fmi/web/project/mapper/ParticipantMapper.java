package bg.sofia.uni.fmi.web.project.mapper;

import bg.sofia.uni.fmi.web.project.dto.ParticipantDto;
import bg.sofia.uni.fmi.web.project.model.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ParticipantMapper {

    final private UserMapper userMapper;
    final private EventMapper eventMapper;

    @Autowired
    public ParticipantMapper(UserMapper userMapper, EventMapper eventMapper) {
        this.userMapper = userMapper;
        this.eventMapper = eventMapper;
    }

    public ParticipantDto toDto(Participant entity) {

        ParticipantDto toReturnDto = new ParticipantDto();

        if (entity.getAssociatedUser() != null) {
            toReturnDto.setAssociatedUserDto(new UserMapper().toDto(entity.getAssociatedUser()));
        }

        if (entity.getAssociatedEvent() != null) {
            toReturnDto.setAssociatedEventDto(new EventMapper().toDto(entity.getAssociatedEvent()));
        }

        toReturnDto.setId(entity.getId());
        toReturnDto.setUserRole(entity.getUserRole());

        return toReturnDto;
    }

    public Participant toEntity(ParticipantDto participantDto) {

        Participant toReturnEntity = new Participant();

        toReturnEntity.setId(participantDto.getId());
        toReturnEntity.setUserRole(participantDto.getUserRole());

        if (participantDto.getAssociatedUserDto() != null) {
            toReturnEntity.setAssociatedUser(userMapper.toEntity(participantDto.getAssociatedUserDto()));
        }

        if (participantDto.getAssociatedEventDto() != null) {
            toReturnEntity.setAssociatedEvent(eventMapper.toEntity(participantDto.getAssociatedEventDto()));
        }

        return toReturnEntity;
    }
}