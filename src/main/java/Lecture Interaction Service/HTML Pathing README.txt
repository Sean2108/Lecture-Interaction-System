Extra Info
Logo -> Our lecture interaction system logo
UOIT -> UOIT emblem

Professor Side

LoginPage ---PASS--> QuestionSetup (Allows professor to set up the question to be answered by students either MC(MultipleChoice) or SA (short Answer))
LoginPage ---FAIL--> LoginPage2 (Same page but including an error message if credentials aren't correct)
LoginPage2 ---PASS--> QuestionSetup (See above)
LoginPage2 ---FAIL--> LoginPage2 (Loops until prof enters vaild credentials)
QuestionSetup ---PASS--> ProfConfirmation (Tells prof question has been submitted to the students)
ProfConfirmation ---PASS--> QuestionSetup (Allows prof to enter in new question)

Session will terminate once the prof closes the window.


Student/Participant side

StudentEnterIp ---FAIL--> SessionNotFound (Will loop on session not found until a session is found)
StudentEnterIP ---PASS--> AnswerQuestion (Will allow the student to answer the question either MC or SA)
SessionNotFound ---PASS--> AnswerQuestion (See above)
AnswerQuestion ---PASS--> QuestionQueue (Temporary HTML Screen which waits for the next question and shows results of the question just answered)
QuestionQueue ---PASS--> AnswerQuestion (Student/participant answers the next questions)
QuestionQueue ---FAIL--> QuestionQueue (Question wasn't ready yet, so it cycles the html page)


Session will terminate once the student/participant closes the window.