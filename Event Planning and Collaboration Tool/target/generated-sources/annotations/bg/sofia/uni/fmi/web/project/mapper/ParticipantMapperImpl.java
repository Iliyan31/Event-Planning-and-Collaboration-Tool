package bg.sofia.uni.fmi.web.project.mapper;

import bg.sofia.uni.fmi.web.project.dto.ParticipantDto;
import bg.sofia.uni.fmi.web.project.model.Participant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-30T18:19:39+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 19.0.1 (Oracle Corporation)"
)
@Component
public class ParticipantMapperImpl implements ParticipantMapper {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EventMapper eventMapper;

    @Override
    public ParticipantDto toDto(Participant model) {
        if ( model == null ) {
            return null;
        }

        ParticipantDto.ParticipantDtoBuilder participantDto = ParticipantDto.builder();

        participantDto.associatedUserDto( userMapper.toDto( model.getAssociatedUser() ) );
        participantDto.associatedEventDto( eventMapper.toDto( model.getAssociatedEvent() ) );
        participantDto.id( model.getId() );
        participantDto.userRole( model.getUserRole() );
        participantDto.createdBy( model.getCreatedBy() );
        participantDto.creationTime( model.getCreationTime() );
        participantDto.updatedBy( model.getUpdatedBy() );
        participantDto.lastUpdatedTime( model.getLastUpdatedTime() );

        return participantDto.build();
    }

    @Override
    public Participant toEntity(ParticipantDto dto) {
        if ( dto == null ) {
            return null;
        }

        Participant.ParticipantBuilder participant = Participant.builder();

        participant.associatedUser( userMapper.toEntity( dto.getAssociatedUserDto() ) );
        participant.associatedEvent( eventMapper.toEntity( dto.getAssociatedEventDto() ) );
        participant.id( dto.getId() );
        participant.userRole( dto.getUserRole() );
        participant.createdBy( dto.getCreatedBy() );
        participant.creationTime( dto.getCreationTime() );
        participant.updatedBy( dto.getUpdatedBy() );
        participant.lastUpdatedTime( dto.getLastUpdatedTime() );

        return participant.build();
    }

    @Override
    public List<ParticipantDto> toDtoList(List<Participant> modelList) {
        if ( modelList == null ) {
            return null;
        }

        List<ParticipantDto> list = new ArrayList<ParticipantDto>( modelList.size() );
        for ( Participant participant : modelList ) {
            list.add( toDto( participant ) );
        }

        return list;
    }
}
