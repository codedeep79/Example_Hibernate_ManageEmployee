package hibernate_example;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;

/**
 *
 * @author TRUNGNGUYENHAU
 */
public class Hibernate_Example {

    private static SessionFactory factory;
    private static ServiceRegistry registry;
    private static final Scanner in = new Scanner(System.in);
    

    public Hibernate_Example() {
    }
    
    static void menu(){
        Integer employeeID = null;
        System.out.println("------- Chọn Công Việc Cần Làm -------");
        System.out.println("1. Thêm Nhân Viên");
        System.out.println("2. Cập Nhật Thông tin Nhân Viên");
        System.out.println("3. Xóa Nhân Viên");
        System.out.println("4. Danh Sách Nhân Viên");
        System.out.println("5. Thoát");
        int chon = in.nextInt();
        switch(chon){
            case 1:
                addEmployee();
                break;
            case 2:
                System.out.println("Nhập Employee ID: ");
                employeeID = in.nextInt();
                System.out.println("Nhập Lương Nhân Viên: ");
                int salary  = in.nextInt();
                updateEmployee(employeeID ,salary);
                break;
            case 3:
                System.out.println("Nhập Employee ID: ");
                employeeID = in.nextInt();
                break;
            case 4:
                System.out.println("----- Danh Sách Nhân Viên -----");
                listEmployees();
                break;
            case 5:
                
                break;
            default:
                StandardServiceRegistryBuilder.destroy(registry);
                break;
        }
    }
    public static void main(String[] args) {
        try {
            Configuration conf = new Configuration().configure();
            registry = new StandardServiceRegistryBuilder()
                    .applySettings(conf.getProperties())
                    .build();
            factory = conf.buildSessionFactory(registry);
        } catch (Throwable ex) {
            System.err.println("Failed to created sessionFactory object. " + ex);
            throw new ExceptionInInitializerError(ex);
        }

        String more = "yes";
        while(more.equalsIgnoreCase("yes")){
            System.out.println("Nhan Yes De Tiep Tuc");
            menu();
            
            System.out.println("Nhan \"Yes\" De Tiep Tuc");
            more = in.nextLine().toLowerCase();
        }
        
        System.out.println("Bạn Đã Thoát!");
    }

    private static Integer addEmployee() {
        in.nextLine();
        System.out.println("Enter your first name: ");
        String fname = in.nextLine();
        
        System.out.println("Enter your last name: ");
        String lname = in.nextLine();
        in.nextLine();
        System.out.println("Enter your cell phone: ");
        String cell = in.nextLine();
        
        System.out.println("Enter home phone: ");
        String hPhone = in.nextLine();
        
        System.out.println("Enter salary: ");
        int salary = in.nextInt();
        in.nextLine();

//        Lớp HashSet trong Java kế thừa AbstractSet và triển khai Set Interface. 
//        Nó tạo một collection mà sử dụng một hash table để lưu giữ. Lưu ý trong hashset
//        không chấp nhận 2 phần tử trùng nhau
//        Một hash table lưu giữ thông tin bởi sử dụng một kỹ thuật được gọi là hashing. Trong
//        hashing, nội dung mang tính thông tin của một key được sử dụng để quyết định một value 
//        duy nhất, được gọi là hash code của nó.
        HashSet hs = new HashSet();
        hs.add(new Phone(cell));
        hs.add(new Phone(hPhone));

        Session session = factory.openSession();
        Transaction transaction = null;
        Integer employeeID = null; // Hibernate chỉ làm việc với object
        try {
            transaction = session.beginTransaction();
            Employee employee = new Employee(fname, lname, salary);
            employee.setPhones(hs);
            employeeID = (Integer) session.save(employee);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
        return employeeID;
    }

    public static void listEmployees() {
        Session session = factory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

//           List employees 
//               = session.createQuery("SELECT e.firstname FROM Employee as e").list();
//           List employees
//               = session.createQuery("FROM Employee as e WHERE e.firstname "
//                 + "like 's%' and salary > 10000000").list();

//           +++++ Name parameters +++++
//           String hql = "FROM Employee as e WHERE salary > :salary";
//           Query query = session.createQuery(hql);
//           query.setInteger("salary", 5000);
//           List employees = query.list();

//          +++++ Aggregate method +++++
//          String hql = "SELECT max(e.salary) FROM Employee as e";
//          Query query = session.createQuery(hql);
//          int s = (int)query.uniqueResult();
//          System.out.print("Max Count Employee: " + s);

//          +++++ Creteria Query API +++++
//          Criteria criteria = session.createCriteria(Employee.class);
//          criteria.add(Restrictions.gt("salary", 5000)); // salary > 5000
//          criteria.addOrder(Order.asc("firstname"));
//          List listEmployee = criteria.list();

            List employees = session.createQuery("FROM Employee").list();
            for (Iterator iterator1
                    = employees.iterator(); iterator1.hasNext();) {
                
//                String fname = (String) iterator1.next();
//                System.out.print("First Name: " + employee.getFirstName());
                Employee employee = (Employee) iterator1.next();
                System.out.print("First Name: " + employee.getFirstName());
                System.out.print("  Last Name: " + employee.getLastName());
                System.out.println("  Salary: " + employee.getSalary());
                Set phoneNums = employee.getPhones();
                for (Iterator iterator2
                        = phoneNums.iterator(); iterator2.hasNext();) {
                    Phone phoneNum = (Phone) iterator2.next();
                    System.out.println("Phone: " + phoneNum.getPhoneNumber());
                }
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
    }

    // Update Salary 
    public static void updateEmployee(Integer EmployeeID, int salary) {
        Session session = factory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Employee employee
                    = (Employee) session.get(Employee.class, EmployeeID);
            employee.setSalary(salary);
            session.update(employee);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
    }

    public static void deleteEmployee(Integer EmployeeID) {
        Session session = factory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Employee employee
                    = (Employee) session.get(Employee.class, EmployeeID);
            session.delete(employee);
            transaction.commit();
        }
        catch (HibernateException e) 
        {
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
    }

}
