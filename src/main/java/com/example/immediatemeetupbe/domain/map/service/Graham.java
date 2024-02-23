package com.example.immediatemeetupbe.domain.map.service;

import com.example.immediatemeetupbe.domain.map.vo.Point;
import com.example.immediatemeetupbe.domain.participant.entity.Participant;
import java.util.*;
import java.io.*;
import org.springframework.stereotype.Service;

@Service
public class Graham {

    //first = y좌표가 가장 작은 점이나  x좌표값이 가장 작은 점을 기준점으로 잡음
    Point first = new Point(Long.MAX_VALUE, Long.MAX_VALUE);

    public ArrayList<Point> calculate(List<Participant> participantList) {

        ArrayList<Point> arr = new ArrayList<Point>();
        // 1. 점들을 입력받는다.

        for (Participant participant : participantList) {
            arr.add(new Point(participant.getLongitude(), participant.getLatitude()));
        }

        // w. 기준점을 구한다.기준점은 y좌표가 작은순 -> x좌표가 작은 순
        for (Point point : arr) {
            if (point.getY() < first.getY()
                || (point.getY() == first.getY() && point.getX() < first.getX())) {
                first = point;
            }
        }

        //3. 기준점과 나머지점들이 ccw로 반시계방향(좌회전)이 되도록 정렬을 시키고, 만약 일직선상에 있으면 거리가 증가하게끔 정렬을 시킴
        arr.sort(new Comparator<Point>() {

            @Override
            public int compare(Point second, Point third) {
                int ccw_Result = ccw(first, second, third);

                if (ccw_Result > 0) {
                    return -1; // second -> third
                } else if (ccw_Result < 0) {
                    return 1; // third -> second
                } else {
                    long dist1 = dist(first, second);
                    long dist2 = dist(first, third);
                    return Long.compare(dist1, dist2);
                }
            }

        });

        /*
        for(int i = 0 ; i < arr.size(); i++)
            System.out.println(arr.get(i).x + " " + arr.get(i).y);
        */

        //4. 그라함 스캔 Start
        Stack<Point> s = new Stack<Point>();
        s.add(first);

        for (int i = 1; i < arr.size(); i++) {
            while (s.size() > 1 && ccw(s.get(s.size() - 2), s.get(s.size() - 1), arr.get(i)) <= 0) {
                s.pop();
            }
            s.add(arr.get(i));
        }

        return arr;

    }


    static int ccw(Point a, Point b, Point c) {
        long n =
            (a.getX() * b.getY() + b.getX() * c.getY() + c.getX() * a.getY()) - (b.getX() * a.getY()
                + c.getX() * b.getY() + a.getX() * c.getY());

        if (n > 0) {
            return 1;
        } else if (n < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    static long dist(Point a, Point b) {
        return (b.getX() - a.getX()) * (b.getX() - a.getX()) + (b.getY() - a.getY()) * (b.getY()
            - a.getY());
    }

}