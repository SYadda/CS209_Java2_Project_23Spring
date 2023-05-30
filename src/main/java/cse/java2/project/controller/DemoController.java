package cse.java2.project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qianxinyao.analysis.jieba.keyword.Keyword;
import com.qianxinyao.analysis.jieba.keyword.TFIDFAnalyzer;
import cse.java2.project.model.Answer;
import cse.java2.project.model.Comment;
import cse.java2.project.model.Owner;
import cse.java2.project.model.Question;
import cse.java2.project.repository.AnswerRepository;
import cse.java2.project.repository.CommentRepository;
import cse.java2.project.repository.OwnerRepository;
import cse.java2.project.serive.QuestionService;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
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

    @GetMapping("/countJavaAPI")
    public String countJavaAPI(Model model) throws JsonProcessingException, FileNotFoundException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Question> questions = questionService.findAllQuestion();

        String filename = "src/main/resources/api.txt"; // The compact3 of https://docs.oracle.com/javase/8/docs/api/
        final String IntSum = "Interface Summary";
        String front_Name = "", last_Line = "", curr_line = "";

        HashMap<String, String> apis = new HashMap<>();
        HashMap<String, Double> apis_count = new HashMap<>();

        try (Scanner sc = new Scanner(new FileReader(filename))) {
            while (sc.hasNextLine()) {
                curr_line = sc.nextLine();
                if (curr_line.equals(IntSum)) {
                    front_Name = last_Line;
                }

                String[] api = curr_line.split("[\\s\\t]");
                if (api.length == 1) {
                    String temp = api[0].split("<")[0];  // HaspMap<K,V> => HashMap
                    apis.put(temp, front_Name);
                    apis_count.put(temp, 0D);
                }
                last_Line = curr_line;
            }
        }

        TFIDFAnalyzer ta = new TFIDFAnalyzer();
        for (Question q: questions) {
            List<Keyword> list = ta.analyze(q.getBody_markdown(), 1000000);
            for(Keyword word: list) {
                String name = word.getName();
                if (apis.containsKey(name)) {
                    apis_count.put(name, apis_count.get(name) + word.getTfidfvalue());
                }
            }
        }

        Map<String, String> display_map = apis_count.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(10)
                .collect(Collectors.toMap(e -> apis.get(e.getKey()) + "." + e.getKey(), e -> String.format("%.2f", e.getValue())));

        model.addAttribute("api_count", objectMapper.writeValueAsString(display_map));
        return "api";
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
        return "acc";
    }

    @GetMapping("content")
    public String getContent(Model model) { return "content"; }
    @GetMapping("countJavaAPI/content")
    public String getContent2(Model model) { return "content"; }
    @GetMapping("user")
    public String getUser(Model model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
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

        //Thread内回答的用户数量
        model.addAttribute("answerThead", objectMapper.writeValueAsString(answerThead));
        //Thread内评论的用户数量
        model.addAttribute("commentThead", objectMapper.writeValueAsString(commentThread));
        //用户的回答数量分布
        model.addAttribute("answerUser", objectMapper.writeValueAsString(getUserCount(ansOwnerActive)));
        //用户的评论数量分布
        model.addAttribute("commentUser", objectMapper.writeValueAsString(getUserCount(comOwnerActive)));
        //回答活跃者
        model.addAttribute("ansTop10", objectMapper.writeValueAsString(getTop10(ansOwnerActive)));
        //评论活跃者
        model.addAttribute("comTop10", objectMapper.writeValueAsString(getTop10(comOwnerActive)));

        return "user";
    }
    private Map<String, Integer> getTop10(TreeMap<String, Integer> oa) {
        TreeMap<String, Integer> sorted_oa = new TreeMap<>(new ValueComparator(oa));
        sorted_oa.putAll(oa);
        return sorted_oa.entrySet().stream().limit(10)
                .collect(Collectors.toMap(e -> e.getKey().split(" ")[0], Map.Entry::getValue));
        //取名字中的第一个词，方便显示
    }
    private Map<String, Integer> getUserCount(TreeMap<String, Integer> oa) {
        TreeMap<String, Integer> user = new TreeMap<>();
        oa.forEach((key, value) -> {
            String s = String.valueOf(value);
            if (user.containsKey(s)) {
                user.put(s, user.get(s) + 1);
            } else {
                user.put(s, 1);
            }
        });
        return user;
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
