import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Session{
    // Responses
    static final String RES_OK = "ok",
                        RES_FAIL = "fail";

    // Requests
    static final String REQ_LOGIN = "login",
                        REQ_NEW_ORDER = "new_order",
                        REQ_TB_CALL = "tb_call",                /* Sequence 1, 3, 10 */
                        REQ_EDIT_ORDER = "edit_order",          /* Sequence 1 */
                        REQ_SEND_ORDER = "send_order",          /* Sequence 2 */
                        REQ_AUTO_ASSIGN = "auto_assign",        /* Sequence 2 */
                        REQ_MAN_ASSIGN = "man_assign",          /* Sequence 2 */
                        REQ_MARK_READY = "mark_ready",          /* Sequence 8 */
                        REQ_PAY_ORDER = "pay_order",            /* Sequence 3 */
                        REQ_ADD_TO_WL = "add_to_wl",            /* Sequence 4 */
                        REQ_SET_RESERVED = "set_reserved",      /* Sequence 5 */
                        REQ_EDIT_EMPLOYEE = "edit_employee",    /* Sequence 6 */
                        REQ_EDIT_TOPOLOGY = "edit_topology",    /* Sequence 7 */
                        REQ_EDIT_PRODUCT = "edit_product",      /* Sequence 9 */
                        REQ_GET_OFFER = "get_offer";            /* Sequence 10 */

    private Socket socket;
    private PrintWriter sock_in;
    private BufferedReader sock_out;
    private boolean valid = false;
    private Device device;

    Session(Socket sock){
        this.socket = sock;

        try{
            // Open in and out channels
            sock_in = new PrintWriter(sock.getOutputStream());
            sock_out = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            valid = true;
        }catch(IOException e){
            e.printStackTrace();
            sock_in = null;
            sock_out = null;
        }
    }

    void start(){
        if(!valid){
            System.out.println("[E] Tried to start an invalid session");
            return;
        }

        new Thread(()->{

            try{
                String req = sock_out.readLine();
                while(req!=null && socket.isConnected()){
                    if(serveRequest(req)) break;
                    req = sock_out.readLine();
                }

            }catch(IOException e){
                e.printStackTrace();
            }

            close();

        }).start();
    }
    void respond(String str){
        sock_in.println(str);
        sock_in.flush();
    }
    void close(){
        System.out.println("[-] Closing connection " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        try{
            sock_in.close();
            sock_out.close();
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    String readString() throws IOException{ return sock_out.readLine(); }
    long readLong() throws IOException{ return Long.parseLong(sock_out.readLine()); }
    int readInt() throws IOException{ return Integer.parseInt(sock_out.readLine()); }
    double readDouble() throws IOException{ return Double.parseDouble(sock_out.readLine()); }
    boolean readBoolean() throws IOException{ return Boolean.parseBoolean(sock_out.readLine()); }

    boolean serveRequest(String req){
        // Serves the client request 'req'
        // Returns whether the caller should close the connection

        try{
            switch(req){
                case REQ_LOGIN:{
                    /*
                     * Format:
                     * type\n  <-- waiter | pr | prepArea
                     * username;password_hash\n
                     *
                     * Respond with waiter id or fail
                     */
                    String[] data = readString().split(";");

                    /*
                     * Implementation starts here
                     */

                    switch(data[0]){
                        case "waiter":{
                            String username = data[1];
                            String pass_hash = data[2];
                            // Assume we verify this with the database, also get is_new and id
                            long id = 5;

                            Waiter w = new Waiter(id, false);               // This is added to allWaiters by the constructor
                            device = new MobileDevice(w, socket.getInetAddress());
                            w.setDevice((MobileDevice)device);
                            respond(Long.toString(id));
                            break;
                        }

                        case "pr":{
                            String username = data[1];
                            String pass_hash = data[2];
                            // Assume we verify this with the database, also get is_new and id
                            long id = 5;

                            PR pr = new PR(id, false);                      // This is added to allPRs by the constructor
                            device = new MobileDevice(pr, socket.getInetAddress());
                            pr.setDevice((MobileDevice)device);
                            respond(Long.toString(id));
                            break;
                        }

                        case "prepArea":{
                            String username = data[1];
                            String pass_hash = data[2];
                            // Assume we verify this with the database, also get prepArea_id, is_new and id
                            long id = 5;
                            long prepArea_id = 10;

                            PrepAreaEmployee pae = new PrepAreaEmployee(id, false);
                            device = new PrepAreaDevice(pae, PrepArea.getPrepAreaById(prepArea_id), socket.getInetAddress());
                            pae.setDevice((PrepAreaDevice)device);
                            respond(Long.toString(id));
                            break;
                        }

                        default:
                            System.out.println("[E] Received unknown login type: "+data[0]);
                            return true;
                    }
                    break;
                }

                case REQ_TB_CALL:{
                    /*
                     * Format:
                     * table_id\n
                     *
                     * Respond with ok
                     */

                    long id = Long.parseLong(sock_out.readLine());

                    Table t = Table.getTableById(id);
                    if(t==null){
                        System.out.println("[E] TableCall for non-existent table");
                        return true;
                    }

                    /*
                     * Implementation starts here
                     */

                    t.onCall();
                    respond(RES_OK);

                    break;
                }

                case REQ_NEW_ORDER:{
                    /*
                     * Format:
                     * table_id\n
                     *
                     * Create a new order and respond with its id, or failed if the table already has an order
                     */

                    // TODO

                    break;
                }

                case REQ_EDIT_ORDER:{
                    /*
                     * Format:
                     * order_id\n
                     * num_of_products\n
                     * action;product_id
                     * action;product_id
                     * ...
                     *
                     * Respond with ok or failed
                     */

                    ArrayList<Product> products = new ArrayList<>();
                    ArrayList<Integer> actions = new ArrayList<>();

                    Order order = Order.getOrderById(Long.parseLong(sock_out.readLine()));
                    if(order==null){
                        System.out.println("[E] Tried to edit non-existent order");
                        return true;
                    }

                    int num_of_products = Integer.parseInt(sock_out.readLine());
                    String[] data;
                    Product p;
                    int action;
                    for(int i = 0;i<num_of_products;i++){
                        data = sock_out.readLine().split(";");
                        p = Product.getProductById(data[1]);
                        action = Integer.parseInt(data[0]);

                        if(p==null){
                            System.out.println("[E] Invalid product id");
                            return true;
                        }

                        products.add(p);
                        actions.add(action);
                    }

                    /*
                     * Implementation starts here
                     */

                    if(order.onEdit(products, actions)){
                        order.addWaiter((Waiter)(this.device.getEmployee()));
                        respond(RES_OK);
                    }else{
                        respond(RES_FAIL);
                    }
                }

                case REQ_SEND_ORDER:{
                    /*
                     * Format:
                     * order_id\n
                     *
                     * Respond with ok
                     */

                    long order_id = Long.parseLong(sock_out.readLine());

                    Order order = Order.getOrderById(order_id);
                    if(order==null){
                        System.out.println("[E] Tried to send non-existent order");
                        return true;
                    }

                    /*
                     * Implementation starts here
                     */

                    order.send();
                    order.addWaiter((Waiter)(this.device.getEmployee()));
                    respond(RES_OK);

                    break;
                }

                case REQ_AUTO_ASSIGN:{

                    break;
                }

                case REQ_MAN_ASSIGN:{

                    break;
                }

                case REQ_MARK_READY:{
                    /*
                     * Format:
                     * nothing, mark ready any order assigned this prepArea
                     *
                     * Respond with ok
                     */

                    /*
                     * Implementation starts here
                     */

                    for(Order o : Order.allOrders){
                        if(o.getAssignedArea() == ((PrepAreaDevice)(this.device)).getPrepArea()){
                            o.setReady();
                        }
                    }
                    break;
                }

                case REQ_PAY_ORDER:{
                    /*
                     * Format:
                     * table_id\n
                     * num_of_products\n
                     * payment_type\n    <-- 0:cash, -1:pos, 1+:billing_account_id
                     * product_id\n
                     * product_id\n
                     * ...
                     *
                     * Respond with ok when done
                     */

                    long table_id = Long.parseLong(sock_out.readLine());

                    Table table = Table.getTableById(table_id);
                    if(table==null){
                        System.out.println("[E] Tried to pay for non-existent table");
                        return true;
                    }

                    int payment_type = readInt();

                    ArrayList<Product> products = new ArrayList<>();
                    int num_of_products = Integer.parseInt(sock_out.readLine());

                    for(int i = 0;i<num_of_products;i++){
                        products.add(Product.getProductById(sock_out.readLine()));
                    }

                    /*
                     * Implementation starts here
                     */

                    table.onOrderPaid(products);
                    respond(RES_OK);

                    double total = 0.0;
                    for(Product p : products){
                        total += p.price;
                    }

                    Waiter w = (Waiter)(this.device.getEmployee());
                    switch(payment_type){
                        case 0:
                            w.addCash(total);
                            break;

                        case 1:
                            w.addPos(total);
                            break;

                        default:
                            BillingAccount ba = BillingAccount.getAccountById(payment_type);
                            if(ba==null){
                                System.out.println("[E] Tried to charge a non-existent billing account");
                                return true;
                            }
                            ba.charge(total);
                            w.addBa(total);

                    }

                    break;
                }

                case REQ_ADD_TO_WL:{
                    /*
                     * Format:
                     * name\n
                     * num_of_people\n
                     * priority\n
                     *
                     * Respond with ok
                     */

                    String name = sock_out.readLine();
                    int num_of_people = Integer.parseInt(sock_out.readLine());
                    int priority = Integer.parseInt(sock_out.readLine());

                    /*
                     * Implementation starts here
                     */

                    WaitingGroup wg = new WaitingGroup(name, num_of_people, priority);
                    WaitingGroup.addToList(wg);
                    wg.notifyWhenAvailable();
                    respond(RES_OK);

                    break;
                }

                case REQ_SET_RESERVED:{
                    /*
                     * Format:
                     * table_id\n
                     *
                     * Repond with ok or failed
                     */

                    long table_id = Long.parseLong(sock_out.readLine());

                    Table table = Table.getTableById(table_id);
                    if(table==null){
                        System.out.println("[E] Tried to setReserved a non-existent table");
                        return true;
                    }

                    /*
                     * Implementation starts here
                     */

                    if(table.isAvailable()){
                        table.setReserved();
                        respond(RES_OK);
                    }else{
                        respond(RES_FAIL);
                    }

                    break;
                }

                case REQ_EDIT_EMPLOYEE:{
                    /*
                     * Format:
                     * employee_id\n
                     * is_new\n  <-- True/False
                     * ...any other properties
                     *
                     * Respond with ok or failed
                     */

                    long employee_id = Long.parseLong(sock_out.readLine());
                    Employee empl = Employee.getEmployeeById(employee_id);

                    if(empl==null){
                        System.out.println("[E] Tried to edit non-existent employee");
                        return true;
                    }

                    Bundle bundle = new Bundle();
                    bundle.put("is_new", Boolean.parseBoolean(sock_out.readLine()));

                    /*
                     * Implementation starts here
                     */

                    respond( empl.editData(bundle) ? RES_OK : RES_FAIL);

                    break;
                }

                case REQ_EDIT_TOPOLOGY:{
                    /*
                     * Format:
                     * no idea
                     *
                     * Reponds with ok or failed
                     */

                    Bundle bundle = new Bundle();
                    // Fill bundle with data from sock_out

                    /*
                     * Implementation starts here
                     */

                    respond( Table.onTopologyEdit(bundle) ? RES_OK : RES_FAIL);

                    break;
                }

                case REQ_EDIT_PRODUCT:{
                    /*
                     * Format:
                     * product_id\n
                     * price\n
                     * description\n
                     * ...
                     */

                    String product_id = sock_out.readLine();

                    Product product = Product.getProductById(product_id);
                    if(product==null){
                        System.out.println("[E] Tried to edit non-existent product");
                        return true;
                    }

                    Bundle bundle = new Bundle();
                    bundle.put("price", Double.parseDouble(sock_out.readLine()));
                    bundle.put("description", sock_out.readLine());

                    /*
                     * Implementation starts here
                     */

                    respond( product.onEdit(bundle) ? RES_OK : RES_FAIL );

                    break;
                }

                case REQ_GET_OFFER:{
                    /*
                     * Format:
                     * table_id\n
                     *
                     * Respond with product_id or fail
                     */

                    long table_id = readLong();
                    Table table = Table.getTableById(table_id);
                    if(table==null){
                        System.out.println("[E] Tried to get offer for non-existent table");
                        return true;
                    }

                    /*
                     * Implementation starts here
                     */

                    long client_id = table.getClient();
                    if(client_id != -1){
                        Product offer = Product.getOfferForClient(client_id);
                        if(offer!=null){
                            respond(offer.id);
                            break;
                        }
                    }

                    respond(RES_FAIL);

                    break;
                }

                default:
                    sock_in.println("STATUS 499 Disappointed: The server has received your request but thinks you can do better.");
                    return true;

            }
        }catch(IOException | NumberFormatException e){
            e.printStackTrace();
            return true;
        }

        return false;
    }
}
