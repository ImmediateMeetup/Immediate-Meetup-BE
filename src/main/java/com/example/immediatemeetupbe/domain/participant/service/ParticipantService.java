package com.example.immediatemeetupbe.domain.participant.service;

import com.example.immediatemeetupbe.domain.meeting.entity.Meeting;
import com.example.immediatemeetupbe.domain.member.entity.Member;
import com.example.immediatemeetupbe.domain.participant.dto.request.ParticipantTimeRequest;
import com.example.immediatemeetupbe.domain.participant.dto.response.ParticipantResponse;
import com.example.immediatemeetupbe.domain.participant.dto.response.TimeTableResponse;
import com.example.immediatemeetupbe.domain.participant.entity.Participant;
import com.example.immediatemeetupbe.domain.participant.entity.ParticipantId;
import com.example.immediatemeetupbe.domain.participant.vo.MeetingTime;
import com.example.immediatemeetupbe.domain.participant.vo.TimeTable;
import com.example.immediatemeetupbe.global.exception.BaseException;
import com.example.immediatemeetupbe.global.exception.BaseExceptionStatus;
import com.example.immediatemeetupbe.global.jwt.AuthUtil;
import com.example.immediatemeetupbe.repository.ParticipantRepository;
import com.example.immediatemeetupbe.repository.MeetingRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final MeetingRepository meetingRepository;
    private final ParticipantRepository ParticipantRepository;
    private final AuthUtil authUtil;
    private final MeetingTime meetingTime;
    private final TimeTable timeTable;


    @Transactional
    public ParticipantResponse registerMemberTime(Long meetingId,
        ParticipantTimeRequest participantTimeRequest) {
        String timeZone = changeTimeToString(participantTimeRequest.getTimeList());
        Member member = authUtil.getLoginMember();
        Meeting meeting = meetingRepository.getById(meetingId);
        Participant participant = participantTimeRequest.toEntity(member, meeting, timeZone);
        ParticipantRepository.save(participant);
        return ParticipantResponse.from(participant.getMember().getId(),
            participant.getMeeting().getId(), participant.getTimeZone());
    }


    @Transactional
    public TimeTableResponse getTimeTable(Long meetingId) {
        Meeting meeting = meetingRepository.getById(meetingId);
        meetingTime.setMeetingTime(meeting.getTimeZone(), meeting.getFirstDay(),
            meeting.getLastDay());
        timeTable.setTimeTable(meetingTime.getFirstDateTime(), meetingTime.getLastDateTime());
        List<Participant> participantList = ParticipantRepository.findAllByMeeting(
            meeting);
        timeTable.calculateSchedule(participantList);

        return TimeTableResponse.from(timeTable.getTimeTable());
    }


    @Transactional
    public ParticipantResponse updateMemberTime(Long meetingId,
        ParticipantTimeRequest participantTimeRequest) {
        Member member = authUtil.getLoginMember();
        Meeting meeting = meetingRepository.getById(meetingId);
        String timeZone = changeTimeToString(participantTimeRequest.getTimeList());
        Participant participant = getMeetingMember(member, meeting);
        participant.registerMemberTime(timeZone);
        return ParticipantResponse.from(participant.getMember().getId(),
            participant.getMeeting().getId(), participant.getTimeZone());
    }

    private static String changeTimeToString(List<LocalDateTime> memberTimeList) {
        return memberTimeList.stream()
            .map(LocalDateTime::toString).collect(
                Collectors.joining("/"));
    }

    private Participant getMeetingMember(Member member, Meeting meeting) {
        return ParticipantRepository.findById(
                new ParticipantId(member, meeting))
            .orElseThrow(() -> new BaseException(
                (BaseExceptionStatus.NO_EXIST_PARTICIPANT.getMessage())));
    }


}
