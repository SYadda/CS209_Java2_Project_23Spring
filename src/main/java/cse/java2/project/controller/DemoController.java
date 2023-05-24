package cse.java2.project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cse.java2.project.model.Answer;
import cse.java2.project.model.Comment;
import cse.java2.project.model.Owner;
import cse.java2.project.model.Question;
import cse.java2.project.repository.AnswerRepository;
import cse.java2.project.repository.CommentRepository;
import cse.java2.project.repository.OwnerRepository;
import cse.java2.project.serive.QuestionService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {


    //调用数据库
    QuestionService questionService;
    private final AnswerRepository answerRepository;
    private final OwnerRepository ownerRepository;
    private final CommentRepository commentRepository;

    /**
     * This method is called when the user requests the root URL ("/") or "/demo". In this demo, you
     * can visit localhost:9090 or localhost:9090/demo to see the result.
     *
     * @return the name of the view to be rendered You can find the static HTML file in
     * src/main/resources/templates/demo.html
     */
    @GetMapping({"/", "/demo"})
    public String demo(Model module) {
        //List<Question> questions = questionService.getQuestions();
        int[] integers = new int[3];
        ArrayList<Long> arrayList = new ArrayList<>();
        questionService.findAllQuestion().forEach(t -> arrayList.add(t.getAnswer_count()));
        integers[0] = 100;
        integers[1] = 120;
        integers[2] = 500;
        module.addAttribute("shuzu", arrayList.toString());
        return "demo";
    }

    @GetMapping("/getPercent")
    public String getAnswerNumber(Model model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        List<Question> questions = questionService.findAllQuestion();
        //添加饼状图的属性列表
        map.put("1个回答", 0);
        map.put("2个回答", 0);
        map.put("3~5个回答", 0);
        map.put("6~10个回答", 0);
        map.put(">10个回答", 0);
        //添加饼状图的值
        questions.forEach(t -> {
            Long ansCount = t.getAnswer_count();
            if (ansCount != 0) {
                if (ansCount == 1) {
                    map.put("1个回答", map.get("1个回答") + 1);
                } else if (ansCount == 2) {
                    map.put("2个回答", map.get("2个回答") + 1);
                } else if (ansCount <= 5) {
                    map.put("3~5个回答", map.get("3~5个回答") + 1);
                } else if (ansCount <= 10) {
                    map.put("6~10个回答", map.get("6~10个回答") + 1);
                } else {
                    map.put(">10个回答", map.get(">10个回答") + 1);
                }
            }
        });
        //分布的饼状图
        model.addAttribute("ansDis", objectMapper.writeValueAsString(map));
        //被回答的饼状图
        model.addAttribute("ansBeReply",
            objectMapper.writeValueAsString(getBeAnsPercent(questions)));
        return "getPercent";
    }

    @GetMapping("/tag")
    public String getTagPage(Model model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        //前十的tag
        Map<String, Long> tagMap = new HashMap<>();
        //前十观看的tags组合
        Map<String, Long> tagMap1 = new HashMap<>();
        //前十点赞的tags组合
        Map<String, Long> tagMap2 = new HashMap<>();
        List<Question> questions = questionService.findAllQuestion();

        Map<String, Long> finalTagMap = tagMap;

        Map<String, Long> finalTagMap1 = tagMap1;

        Map<String, Long> finalTagMap2 = tagMap2;
        questions.forEach(t -> {
            StringBuilder tagString = new StringBuilder();
            t.getTags().stream().sorted().forEach(e -> {
                    //找出前十的与java一起出现的tags
                    tagString.append(e).append("//");

                    if (e.equalsIgnoreCase("java")) {
                        return;
                    }
                    if (finalTagMap.containsKey(e)) {
                        finalTagMap.put(e, finalTagMap.get(e) + 1);
                    } else {
                        finalTagMap.put(e, 1L);
                    }
                }
            );
            if (finalTagMap1.containsKey(tagString.toString())) {
                finalTagMap1.put(tagString.toString(),
                    finalTagMap1.get(tagString.toString()) + t.getView_count());
                finalTagMap2.put(tagString.toString(),
                    finalTagMap2.get(tagString.toString()) + t.getUp_vote_count());
            } else {
                finalTagMap1.put(tagString.toString(), t.getView_count());
                finalTagMap2.put(tagString.toString(), t.getUp_vote_count());
            }

        });

        tagMap = tagMap.entrySet().stream()
            .sorted((o1, o2) -> Long.compare(o2.getValue(), o1.getValue())).limit(10).collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a,
                    TreeMap::new));
        //拿到最多观看数的10个tags并进行处理，让它好看点
        tagMap1 = tagMap1.entrySet().stream()
            .sorted((o1, o2) -> Long.compare(o2.getValue(), o1.getValue())).limit(10).collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a,
                    TreeMap::new));
        //拿到最多点赞的10个tags
        tagMap2 = tagMap2.entrySet().stream()
            .sorted((o1, o2) -> Long.compare(o2.getValue(), o1.getValue())).limit(10).collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a,
                    TreeMap::new));

        TreeMap<String, Long> temp = new TreeMap<>();
        tagMap1.forEach((k, v) -> {
            temp.put(k.replace("//", ","), v);
        });
        tagMap1 = temp;

        TreeMap<String, Long> temp1 = new TreeMap<>();
        tagMap2.forEach((k, v) -> {
            temp1.put(k.replace("//", ","), v);
        });
        tagMap2 = temp1;
        model.addAttribute("tagsMapMostAppear", objectMapper.writeValueAsString(tagMap));
        model.addAttribute("tagsMapMaxView", objectMapper.writeValueAsString(tagMap1));
        model.addAttribute("tagsMapMaxUp", objectMapper.writeValueAsString(tagMap2));
        return "tag";
    }


    public LinkedHashMap<String, Long> getBeAnsPercent(List<Question> questions) {
        LinkedHashMap<String, Long> map = new LinkedHashMap<>();

        long beAns = questions.stream().filter(Question::getIs_answered).count();
        long totalQ = questions.size();
        // double beAnsP = ((double) beAns / (double) totalQ) * 100;
        String beAnsS = "有回答";
        String notBeAnsS = "没有回答";
        map.put(beAnsS, beAns);
        map.put(notBeAnsS, (totalQ - beAns));
        return map;
    }

    @GetMapping("AccA")
    public String getAccA(Model model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Question> questions = questionService.findAllQuestion();
        //接受了问题的数量
        Long count = questions.stream().filter(Question::getHaveAcceptedAns).count();
        //问题二，哪些问题的回答没被ac的答案点赞高于ac的
        Long countVoteMore = questions.stream().filter(Question::getHaveAcceptedAns).filter(
            t -> {
                Answer acAnswer = questionService.findByAnswer_id(t.getAccepted_answer_id());
                AtomicBoolean result = new AtomicBoolean(false);
                List<Answer> answers = answerRepository.findAllByQuestionid(t.getQuestion_id());
                answers.forEach(e -> {
                    if (!Objects.equals(e.getAnswer_id(), acAnswer.getAnswer_id())
                        && e.getUp_vote_count() > acAnswer.getUp_vote_count()) {
                        result.set(true);
                    }
                });
                return result.get();
            }
        ).count();

        ArrayList<Long> longs = new ArrayList<>();
        questions.stream().filter(Question::getHaveAcceptedAns).forEach(t -> {
            Long time =
                questionService.findByAnswer_id(t.getAccepted_answer_id()).getCreation_date()
                    - t.getCreation_date();
            longs.add(time);
        });
        //被ac用时
        List<Long> ans = longs.stream().sorted().toList();
        LinkedHashMap<String, Long> linkedHashMap = new LinkedHashMap<>();
        //初始化各个阶段时间
        linkedHashMap.put("1天", 0L);
        linkedHashMap.put("7天", 0L);
        linkedHashMap.put("15天", 0L);
        linkedHashMap.put("30天", 0L);
        linkedHashMap.put("90天", 0L);
        linkedHashMap.put("180天", 0L);
        linkedHashMap.put("365天", 0L);
        linkedHashMap.put("超过一年", 0L);
        ans.forEach(t -> {
            if (t / 86400 < 1) {
                linkedHashMap.put("1天", linkedHashMap.get("1天") + 1);
            } else if (t / 86400 < 7) {
                linkedHashMap.put("7天", linkedHashMap.get("7天") + 1);
            } else if (t / 86400 < 15) {
                linkedHashMap.put("15天", linkedHashMap.get("15天") + 1);
            } else if (t / 86400 < 30) {
                linkedHashMap.put("30天", linkedHashMap.get("30天") + 1);
            } else if (t / 86400 < 90) {
                linkedHashMap.put("90天", linkedHashMap.get("90天") + 1);
            } else if (t / 86400 < 180) {
                linkedHashMap.put("180天", linkedHashMap.get("180天") + 1);
            } else if (t / 86400 < 365) {
                linkedHashMap.put("365天", linkedHashMap.get("365天") + 1);
            } else {
                linkedHashMap.put("超过一年", linkedHashMap.get("超过一年") + 1);
            }

        });
        model.addAttribute("PercentOfAc", (double) count / (double) questions.size());
        model.addAttribute("countVoteMore", (double) countVoteMore / (double) count);
        model.addAttribute("acTimeDis", objectMapper.writeValueAsString(linkedHashMap));
        System.out.println(getThread(null));
        return "acc";
    }


    public String getThread(Model model) {
        List<Question> questions = questionService.findAllQuestion();
        //回答的用户Thread比
        TreeMap<Long, Long> answerThead = new TreeMap<>();
        //评论的用户Thread比
        TreeMap<Long, Long> commentThread = new TreeMap<>();

        questions.stream().filter(t -> t.getAccount_id() != null).forEach(t -> {
            List<Owner> ownersOfAnswer = new ArrayList<>();
            List<Owner> ownerOfComment = new ArrayList<>();
            //按照账号id找到用户

            //获取一个问题所有的回答
            answerRepository.findAllByQuestionid(t.getQuestion_id()).stream()
                .filter(e -> e.getAccount_id() != null).forEach(e -> {
                    //获取每个回答的用户
                    ownersOfAnswer.add(ownerRepository.findAllByAccountid(e.getAccount_id()));
                    //获取对该问题的回答的评论的用户
                    List<Comment> comments = commentRepository.findAllByPostid(
                        e.getAnswer_id());
                    //加到用户list中

                    comments.forEach(s -> {
                        ownerOfComment.add(ownerRepository.findAllByAccountid(s.getAccount_id()));
                    });
                });
            //去重统计这个Thread的参与用户量
            long countOfAnswerOwner = ownersOfAnswer.stream().filter(e -> e.getUser_id() != null)
                .filter(distinctByKey(Owner::getUser_id)).count();
            long countOfCommentOwner = ownerOfComment.stream().filter(Objects::nonNull)
                .filter(e -> e.getUser_id() != null)
                .filter(distinctByKey(Owner::getUser_id)).count();
            addCount(answerThead, countOfAnswerOwner);
            addCount(commentThread, countOfCommentOwner);
        });


        //回答活跃者
        TreeMap<String, Integer> ansOwnerActive = new TreeMap<>();
        //评论活跃者
        TreeMap<String, Integer> comOwnerActive = new TreeMap<>();
        List<Comment> comments = commentRepository.findAll();
        comments.stream().filter(t -> t.getAccount_id() != null && t.account_id != -1)
            .forEach(t -> {
                if (ownerRepository.findAllByAccountid(t.getAccount_id()) != null) {
                    String name = ownerRepository.findAllByAccountid(t.getAccount_id())
                        .getDisplay_name();
                    if (comOwnerActive.containsKey(name)) {
                        comOwnerActive.put(name, comOwnerActive.get(name) + 1);
                    } else {
                        comOwnerActive.put(name, 1);
                    }
                }
            });
        List<Answer> answers = answerRepository.findAll();
        answers.stream().filter(t -> t.getAccount_id() != null).forEach(t -> {
            String name = ownerRepository.findAllByAccountid(t.getAccount_id()).getDisplay_name();
            if (ansOwnerActive.containsKey(name)) {
                ansOwnerActive.put(name, ansOwnerActive.get(name) + 1);
            } else {
                ansOwnerActive.put(name, 1);
            }
        });

        ValueComparator comparator = new ValueComparator(ansOwnerActive);
        TreeMap<String, Integer> finalSortedMapOfAns = new TreeMap<>(comparator);
        finalSortedMapOfAns.putAll(ansOwnerActive);
        ValueComparator comparator1 = new ValueComparator(comOwnerActive);
        TreeMap<String, Integer> finalSortedMapOfCom = new TreeMap<>(comparator1);
        finalSortedMapOfCom.putAll(comOwnerActive);




        //回答的用户Thread比
        System.out.println(answerThead);
        //评论的用户Thread比
        System.out.println(commentThread);
        //回答活跃者
        System.out.println(finalSortedMapOfAns);
        //评论活跃者
        System.out.println(finalSortedMapOfCom);
        return null;
    }


    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private static void addCount(TreeMap<Long, Long> q1, long count) {
        if (q1.containsKey(count)) {
            q1.put(count, q1.get(count) + 1);
        } else {
            q1.put(count, 1L);
        }
    }


    @Autowired
    public DemoController(QuestionService questionService,
        AnswerRepository answerRepository,
        OwnerRepository ownerRepository,
        CommentRepository commentRepository) {
        this.questionService = questionService;
        this.answerRepository = answerRepository;
        this.ownerRepository = ownerRepository;
        this.commentRepository = commentRepository;
    }


    class ValueComparator implements Comparator<String> {

        Map<String, Integer> map;

        public ValueComparator(Map<String, Integer> map) {
            this.map = map;
        }

        @Override
        public int compare(String a, String b) {
            if (map.get(a) >= map.get(b)) {
                return -1;
            } else {
                return 1;
            }
        }
    }


}
