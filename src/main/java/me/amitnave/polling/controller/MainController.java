package me.amitnave.polling.controller;

import me.amitnave.polling.command.handler.CommandHandlerChecker;
import me.amitnave.polling.command.handler.Settings;
import me.amitnave.polling.model.Member;
import me.amitnave.polling.model.Poll;
import me.amitnave.polling.repo.MemberRepository;
import me.amitnave.polling.repo.PollOptionRepository;
import me.amitnave.polling.repo.PollRepository;
import me.amitnave.polling.repo.PollVoteRepository;
import me.amitnave.polling.whatsapp.Message;
import me.amitnave.polling.whatsapp.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.TemporalAmount;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path="/api")
public class MainController {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PollRepository pollRepository;
    @Autowired
    private PollOptionRepository pollOptionRepository;
    @Autowired
    private PollVoteRepository pollVoteRepository;

    @GetMapping(path="/member_list")
    public @ResponseBody Iterable<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @PostMapping(path="/new")
    public @ResponseBody
    List<Response> postMessage(@RequestBody Message message) {
        CommandHandlerChecker checker = new CommandHandlerChecker(memberRepository,
                pollRepository,
                pollOptionRepository,
                pollVoteRepository);

        return checker.processMessage(message);
    }

    public final static long MILLIS_PER_WEEK =  7 * 24 * 60 * 60 * 1000;

    @PostMapping(path="/check")
    public @ResponseBody
    List<Response> checkUpdates() {
        Date date = new Date(System.currentTimeMillis() - MILLIS_PER_WEEK);
        List<Poll> updatedPolls = pollRepository.findAllByTimestampBefore(date);
        updatedPolls.forEach(poll -> poll.setResult(poll.getWinner()));
        pollRepository.saveAll(updatedPolls);
        return updatedPolls.stream()
                .map(Poll::displayPassed)
                .map(display -> new Response(Settings.POLLS_CHAT, display, null)).collect(Collectors.toList());
    }
}
