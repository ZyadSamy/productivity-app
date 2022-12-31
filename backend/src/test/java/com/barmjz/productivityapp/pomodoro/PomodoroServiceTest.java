package com.barmjz.productivityapp.pomodoro;
import com.barmjz.productivityapp.todo_task_category.task.OneTimeTask;
import com.barmjz.productivityapp.user.User;
import com.barmjz.productivityapp.user.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class PomodoroServiceTest {
    @Autowired
    UserRepo userRepo;

    @Mock
    PomodoroRepo pomoRepo;

    User user;
    PomodoroService underTest;

    @BeforeEach
    void setUp() {
        underTest = new PomodoroService(pomoRepo);
        user = User.builder()
                .email("user1@gmail.com")
                .password("pass1")
                .firstName("user1First")
                .lastName("user1Last")
                .build();
        userRepo.save(user);
    }

    //Testing get
    @Test
    void CheckPomodoroRetrievedWhenFirstTime(){
        //given
        Pomodoro firstPomo = Pomodoro.builder().id(1L).studyTime(25).breakTime(5).user(user).build();

        //when
        underTest.get(user);

        //then
        ArgumentCaptor<Pomodoro> pomodoroArgumentCaptor = ArgumentCaptor.forClass(Pomodoro.class);
        verify(pomoRepo).save(pomodoroArgumentCaptor.capture());
        assertThat(pomodoroArgumentCaptor.getValue()).isEqualTo(firstPomo);
    }

    @Test
    void CheckPomodoroRetrievedWhenNthTime(){
        //given
        Pomodoro p = Pomodoro.builder().id(1L).studyTime(25).breakTime(5).user(user).build();
        given(pomoRepo.existsPomodoroByUserId(user.getId())).willReturn(true);
        given(pomoRepo.getPomodoroByUserId(user.getId())).willReturn(p);

        //when
        Pomodoro returnedP = underTest.get(user);

        //then
        assertThat(returnedP).isEqualTo(p);
    }

    @Test
    void CheckSessionRetrievedWhenPaused(){
        //given
        PomodoroSession p = PomodoroSession
                .builder()
                .id(1L)
                .studyTime(25)
                .breakTime(5)
                .isPaused(true)
                .user(user).build();
        given(pomoRepo.existsPomodoroByUserId(user.getId())).willReturn(true);
        given(pomoRepo.getPomodoroByUserId(user.getId())).willReturn(p);

        //when
        Pomodoro returnedP = underTest.get(user);

        //then
        assertThat(returnedP).isEqualTo(p);
    }

    @Test
    void CheckSessionRunningInBackground(){
        //given
        PomodoroSession p = PomodoroSession
                .builder()
                .id(1L)
                .studyTime(25)
                .breakTime(5)
                .isPaused(false)
                .isStudying(true)
                .startTime(System.currentTimeMillis()/1000 - 20*60L)
                .remainingTimeInSecs(25*60L)
                .user(user).build();
        given(pomoRepo.existsPomodoroByUserId(user.getId())).willReturn(true);
        given(pomoRepo.getPomodoroByUserId(user.getId())).willReturn(p);

        //when
        PomodoroSession returnedP = (PomodoroSession) underTest.get(user);

        //setting expected
        p.setRemainingTimeInSecs(5*60L);

        //then
        assertThat(returnedP).isEqualTo(p);
    }

    //Testing endStudy
    @Test
    void CheckStudySessionEndedWithoutPause(){
        //given
        PomodoroSession p = PomodoroSession
                .builder()
                .id(1L)
                .studyTime(25)
                .breakTime(5)
                .isPaused(false)
                .isStudying(true)
                .startTime(System.currentTimeMillis()/1000 - 25*60L)
                .remainingTimeInSecs(25*60L)
                .user(user).build();
        given(pomoRepo.getPomodoroByUserId(user.getId())).willReturn(p);

        //when
        underTest.endStudy(user.getId());

        //setting expected
        p.setPaused(true);
        p.setRemainingTimeInSecs(p.breakTime*60L);
        p.setStudying(false);
        p.setStartTime(System.currentTimeMillis()/1000);

        //then
        ArgumentCaptor<Pomodoro> pomodoroArgumentCaptor = ArgumentCaptor.forClass(Pomodoro.class);
        verify(pomoRepo).save(pomodoroArgumentCaptor.capture());
        assertThat(pomodoroArgumentCaptor.getValue()).isEqualTo(p);
    }

    @Test
    void CheckStudySessionEndedWithPauses(){
        //given
        PomodoroSession p = PomodoroSession
                .builder()
                .id(1L)
                .studyTime(25)
                .breakTime(5)
                .isPaused(false)
                .isStudying(true)
                .startTime(System.currentTimeMillis()/1000 - 15*60L)
                .remainingTimeInSecs(15*60L)
                .user(user).build();
        given(pomoRepo.getPomodoroByUserId(user.getId())).willReturn(p);

        //when
        underTest.endStudy(user.getId());

        //setting expected
        p.setPaused(true);
        p.setRemainingTimeInSecs(p.breakTime*60L);
        p.setStudying(false);
        p.setStartTime(System.currentTimeMillis()/1000);

        //then
        ArgumentCaptor<Pomodoro> pomodoroArgumentCaptor = ArgumentCaptor.forClass(Pomodoro.class);
        verify(pomoRepo).save(pomodoroArgumentCaptor.capture());
        assertThat(pomodoroArgumentCaptor.getValue()).isEqualTo(p);
    }

    @Test
    void CheckStudySessionEndedLate(){
        //given
        PomodoroSession p = PomodoroSession
                .builder()
                .id(1L)
                .studyTime(25)
                .breakTime(5)
                .isPaused(false)
                .isStudying(true)
                .startTime(System.currentTimeMillis()/1000 - 30*60L)
                .remainingTimeInSecs(25*60L)
                .user(user).build();
        given(pomoRepo.getPomodoroByUserId(user.getId())).willReturn(p);

        //when
        underTest.endStudy(user.getId());

        //setting expected
        p.setPaused(true);
        p.setRemainingTimeInSecs(p.breakTime*60L);
        p.setStudying(false);
        p.setStartTime(System.currentTimeMillis()/1000);

        //then
        ArgumentCaptor<Pomodoro> pomodoroArgumentCaptor = ArgumentCaptor.forClass(Pomodoro.class);
        verify(pomoRepo).save(pomodoroArgumentCaptor.capture());
        assertThat(pomodoroArgumentCaptor.getValue()).isEqualTo(p);
    }

    //Testing reset
    @Test
    void CheckResetWhileStudying(){
        //given
        PomodoroSession p = PomodoroSession
                .builder()
                .id(1L)
                .studyTime(25)
                .breakTime(5)
                .isPaused(false)
                .isStudying(true)
                .startTime(System.currentTimeMillis()/1000 - 25*60L)
                .remainingTimeInSecs(25*60L)
                .user(user).build();
        given(pomoRepo.getPomodoroByUserId(user.getId())).willReturn(p);

        //when
        underTest.reset(user);

        //setting expected
        Pomodoro expected = Pomodoro.builder().id(1L).studyTime(25).breakTime(5).user(user).build();

        //then
        ArgumentCaptor<Pomodoro> pomodoroArgumentCaptor = ArgumentCaptor.forClass(Pomodoro.class);
        verify(pomoRepo).deleteById(1L);
        verify(pomoRepo).save(pomodoroArgumentCaptor.capture());
        assertThat(pomodoroArgumentCaptor.getValue()).isEqualTo(expected);
    }

    @Test
    void CheckResetWhileInBreak(){
        //given
        PomodoroSession p = PomodoroSession
                .builder()
                .id(1L)
                .studyTime(25)
                .breakTime(5)
                .isPaused(false)
                .isStudying(false)
                .startTime(System.currentTimeMillis()/1000 - 5*60L)
                .remainingTimeInSecs(55*60L)
                .user(user).build();
        given(pomoRepo.getPomodoroByUserId(user.getId())).willReturn(p);

        //when
        underTest.reset(user);

        //setting expected
        Pomodoro expected = Pomodoro.builder().id(1L).studyTime(25).breakTime(5).user(user).build();

        //then
        ArgumentCaptor<Pomodoro> pomodoroArgumentCaptor = ArgumentCaptor.forClass(Pomodoro.class);
        verify(pomoRepo).deleteById(1L);
        verify(pomoRepo).save(pomodoroArgumentCaptor.capture());
        assertThat(pomodoroArgumentCaptor.getValue()).isEqualTo(expected);
    }

    @Test
    void CheckResetWhenPausedInStudy(){
        //given
        PomodoroSession p = PomodoroSession
                .builder()
                .id(1L)
                .studyTime(25)
                .breakTime(5)
                .isPaused(true)
                .isStudying(true)
                .startTime(System.currentTimeMillis()/1000 - 400*60L)
                .remainingTimeInSecs(20*60L)
                .user(user).build();
        given(pomoRepo.getPomodoroByUserId(user.getId())).willReturn(p);

        //when
        underTest.reset(user);

        //setting expected
        Pomodoro expected = Pomodoro.builder().id(1L).studyTime(25).breakTime(5).user(user).build();

        //then
        ArgumentCaptor<Pomodoro> pomodoroArgumentCaptor = ArgumentCaptor.forClass(Pomodoro.class);
        verify(pomoRepo).deleteById(1L);
        verify(pomoRepo).save(pomodoroArgumentCaptor.capture());
        assertThat(pomodoroArgumentCaptor.getValue()).isEqualTo(expected);
    }

    @Test
    void CheckResetWhenPausedInBreak(){
        //given
        PomodoroSession p = PomodoroSession
                .builder()
                .id(1L)
                .studyTime(25)
                .breakTime(5)
                .isPaused(true)
                .isStudying(false)
                .startTime(System.currentTimeMillis()/1000 - 400*60L)
                .remainingTimeInSecs(3*60L)
                .user(user).build();
        given(pomoRepo.getPomodoroByUserId(user.getId())).willReturn(p);

        //when
        underTest.reset(user);

        //setting expected
        Pomodoro expected = Pomodoro.builder().id(1L).studyTime(25).breakTime(5).user(user).build();

        //then
        ArgumentCaptor<Pomodoro> pomodoroArgumentCaptor = ArgumentCaptor.forClass(Pomodoro.class);
        verify(pomoRepo).deleteById(1L);
        verify(pomoRepo).save(pomodoroArgumentCaptor.capture());
        assertThat(pomodoroArgumentCaptor.getValue()).isEqualTo(expected);
    }

    @Test
    void CheckResetWhenDoneStudying(){
        //given
        PomodoroSession p = PomodoroSession
                .builder()
                .id(1L)
                .studyTime(25)
                .breakTime(5)
                .isPaused(false)
                .isStudying(true)
                .startTime(System.currentTimeMillis()/1000 - 400*60L)
                .remainingTimeInSecs(25*60L)
                .user(user).build();
        given(pomoRepo.getPomodoroByUserId(user.getId())).willReturn(p);

        //when
        underTest.reset(user);

        //setting expected
        Pomodoro expected = Pomodoro.builder().id(1L).studyTime(25).breakTime(5).user(user).build();

        //then
        ArgumentCaptor<Pomodoro> pomodoroArgumentCaptor = ArgumentCaptor.forClass(Pomodoro.class);
        verify(pomoRepo).deleteById(1L);
        verify(pomoRepo).save(pomodoroArgumentCaptor.capture());
        assertThat(pomodoroArgumentCaptor.getValue()).isEqualTo(expected);
    }

    @Test
    void CheckResetWhenSessionEnds(){
        //given
        PomodoroSession p = PomodoroSession
                .builder()
                .id(1L)
                .studyTime(25)
                .breakTime(5)
                .isPaused(false)
                .isStudying(false)
                .startTime(System.currentTimeMillis()/1000 - 400*60L)
                .remainingTimeInSecs(3*60L)
                .user(user).build();
        given(pomoRepo.getPomodoroByUserId(user.getId())).willReturn(p);

        //when
        underTest.reset(user);

        //setting expected
        Pomodoro expected = Pomodoro.builder().id(1L).studyTime(25).breakTime(5).user(user).build();

        //then
        ArgumentCaptor<Pomodoro> pomodoroArgumentCaptor = ArgumentCaptor.forClass(Pomodoro.class);
        verify(pomoRepo).deleteById(1L);
        verify(pomoRepo).save(pomodoroArgumentCaptor.capture());
        assertThat(pomodoroArgumentCaptor.getValue()).isEqualTo(expected);
    }

    //Testing get cont.
    @Test
    void CheckStudySessionRetrievedWhenEndedLate(){
        //given
        PomodoroSession p = PomodoroSession
                .builder()
                .id(1L)
                .studyTime(25)
                .breakTime(5)
                .isPaused(false)
                .isStudying(true)
                .startTime(System.currentTimeMillis()/1000 - 400*60L)
                .remainingTimeInSecs(25*60L)
                .user(user).build();
        given(pomoRepo.existsPomodoroByUserId(user.getId())).willReturn(true);
        given(pomoRepo.getPomodoroByUserId(user.getId())).willReturn(p);

        //when
        underTest.get(user);

        //setting expected
        p.setStartTime(System.currentTimeMillis()/1000);
        p.setPaused(true);
        p.setStudying(false);
        p.setRemainingTimeInSecs(p.breakTime*60L);

        //then
        ArgumentCaptor<Pomodoro> pomodoroArgumentCaptor = ArgumentCaptor.forClass(Pomodoro.class);
        verify(pomoRepo).save(pomodoroArgumentCaptor.capture());
        assertThat(pomodoroArgumentCaptor.getValue()).isEqualTo(p);
    }

    @Test
    void CheckSessionResetWhenEndedLate(){
        //given
        PomodoroSession p = PomodoroSession
                .builder()
                .id(1L)
                .studyTime(25)
                .breakTime(5)
                .isPaused(false)
                .isStudying(false)
                .startTime(System.currentTimeMillis()/1000 - 400*60L)
                .remainingTimeInSecs(5*60L)
                .user(user).build();
        given(pomoRepo.existsPomodoroByUserId(user.getId())).willReturn(true);
        given(pomoRepo.getPomodoroByUserId(user.getId())).willReturn(p);

        //when
        underTest.get(user);

        //setting expected
        Pomodoro expected = Pomodoro.builder().id(1L).studyTime(25).breakTime(5).user(user).build();

        //then
        ArgumentCaptor<Pomodoro> pomodoroArgumentCaptor = ArgumentCaptor.forClass(Pomodoro.class);
        verify(pomoRepo).deleteById(1L);
        verify(pomoRepo).save(pomodoroArgumentCaptor.capture());
        assertThat(pomodoroArgumentCaptor.getValue()).isEqualTo(expected);
    }

    
}