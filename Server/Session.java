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
    static final String REQ_LOGIN = "login",                                                // correctly returns 5 (dummy id)
                        REQ_NEW_ORDER = "new_order",                                        // correctly creates the order
                        REQ_EXIT = "exit",                                                  // correctly closes the connection
                        REQ_TB_CALL = "tb_call",                /* Sequence 1, 3, 10 */     // correctly sends notification, and re-sends it after rejection
                        REQ_EDIT_ORDER = "edit_order",          /* Sequence 1 */            // correctly edits the order
                        REQ_SEND_ORDER = "send_order",          /* Sequence 2 */            // correctly sends the order, correctly changes the prepArea when it's after rejection
                        REQ_AUTO_ASSIGN = "auto_assign",        /* Sequence 2 */
                        REQ_ASSIGN_ORDERS = "assign_orders",    /* Sequence 2 */
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
            new Exception("Tried to start an invalid session").printStackTrace();
            return;
        }

        new Thread(()->{

            try{
                String req = readString();
                while(req!=null && socket.isConnected()){
                    if(serveRequest(req)) break;
                    req = readString();
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
    long readLong() throws IOException{ return Long.parseLong(readString()); }
    int readInt() throws IOException{ return Integer.parseInt(readString()); }
    double readDouble() throws IOException{ return Double.parseDouble(readString()); }
    boolean readBoolean() throws IOException{ return Boolean.parseBoolean(readString()); }

    boolean serveRequest(String req){
        // Serves the client request 'req'
        // Returns whether the caller should close the connection

        try{
            switch(req){
                case REQ_EXIT:
                    return true;

                case REQ_LOGIN:{
                    /*
                     * Format:
                     * type;username;password_hash\n
                     *
                     * type <-- waiter | pr | prepArea | tableButton
                     *
                     * Respond with waiter id or fail
                     */
                    String[] data = readString().split(";");
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

                        case "tableButton":{
                            long table_id = Long.parseLong(data[1]);
                            String preshared_hash = data[2];
                            // Assume we verify this with the database

                            Table table = Table.getTableById(table_id);
                            if(table==null){
                                new Exception("Tried to log in as tableButton with invalid table_id").printStackTrace();
                                return true;
                            }

                            this.device = new TableButton(socket.getInetAddress(), table);
                        }

                        default:
                            new Exception("Received unknown login type: "+data[0]).printStackTrace();
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

                    long id = readLong();

                    Table t = Table.getTableById(id);
                    if(t==null){
                        new Exception("TableCall for non-existent table").printStackTrace();
                        return true;
                    }

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

                    Table table = Table.getTableById(readLong());
                    if(table==null){
                        new Exception("Asked to create new order for non-existent table").printStackTrace();
                        return true;
                    }

                    if(table.getBalance()>0){
                        new Exception("Table has a pending order, can't create a new one").printStackTrace();
                        respond(RES_FAIL);
                        return true;
                    }

                    long new_id = Order.allOrders.size()+1;          // This should be changed to something more multi-thread-safe
                    table.setOrder( new Order(new_id, table) );
                    respond(Long.toString(table.getOrder().getId()));

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

                    Order order = Order.getOrderById(readLong());
                    if(order==null){
                        new Exception("Tried to edit non-existent order").printStackTrace();
                        return true;
                    }

                    int num_of_products = readInt();
                    String[] data;
                    Product p;
                    int action;
                    for(int i = 0;i<num_of_products;i++){
                        data = readString().split(";");
                        p = Product.getProductById(Long.parseLong(data[1]));
                        action = Integer.parseInt(data[0]);

                        if(p==null){
                            new Exception("Invalid product id").printStackTrace();
                            return true;
                        }

                        products.add(p);
                        actions.add(action);
                    }

                    if(order.onEdit(products, actions)){
                        respond(RES_OK);
                    }else{
                        respond(RES_FAIL);
                    }

                    break;
                }

                case REQ_SEND_ORDER:{
                    /*
                     * Format:
                     * order_id\n
                     *
                     * Respond with ok
                     */

                    long order_id = readLong();

                    Order order = Order.getOrderById(order_id);
                    if(order==null){
                        new Exception("Tried to send non-existent order").printStackTrace();
                        return true;
                    }

                    // If order is already assigned, then it was rejected
                    if(order.getAssignedArea()!=null){
                        order.getAssignedArea().onOrderReject(order);
                    }

                    order.send();
                    order.addWaiter((Waiter)(this.device.getEmployee()));
                    respond(RES_OK);

                    break;
                }

                case REQ_AUTO_ASSIGN:{
                    /*
                     * Format:
                     * nothing
                     *
                     * Respond with list of orders:
                     * num_of_orders\n
                     * order_id\n
                     * order_id\n
                     * ...
                     */

                    PrepArea currentPA = ((PrepAreaDevice)(this.device)).getPrepArea();
                    ArrayList<Order> orders = currentPA.findBestCombination();

                    respond(Integer.toString(orders.size()));
                    for(Order o : orders){
                        respond(Long.toString(o.getId()));
                    }

                    break;
                }

                case REQ_ASSIGN_ORDERS:{
                    /*
                     * Format:
                     * num_of_orders\n
                     * order_id\n
                     * order_id\n
                     * ...
                     *
                     * Respond with ok
                     */

                    int num_of_orders = readInt();
                    for(int i=0; i<num_of_orders; i++){
                        Order o = Order.getOrderById(readLong());

                        if(o!=null){

                            PrepArea best = PrepArea.findBestForOrder(o);
                            if(best==null){
                                new Exception("Called assign_orders without any PrepAreas in the system").printStackTrace();
                                return true;
                            }

                            o.assignOrder(best);

                        }else{
                            new Exception("Tried to assign non-existent order").printStackTrace();
                            return true;
                        }
                    }

                    respond(RES_OK);

                    break;
                }

                case REQ_MARK_READY:{
                    /*
                     * Format:
                     * nothing, mark ready any order assigned this prepArea
                     *
                     * Respond with ok
                     */

                    PrepArea currentPA = ((PrepAreaDevice)(this.device)).getPrepArea();
                    for(Order o : currentPA.getAssignedOrders()){
                        o.setReady();
                    }

                    currentPA.clearAssignedOrders();

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

                    long table_id = readLong();

                    Table table = Table.getTableById(table_id);
                    if(table==null){
                        new Exception("Tried to pay for non-existent table").printStackTrace();
                        return true;
                    }

                    int payment_type = readInt();

                    ArrayList<Product> products = new ArrayList<>();
                    int num_of_products = readInt();

                    for(int i = 0;i<num_of_products;i++){
                        products.add(Product.getProductById(readLong()));
                    }

                    table.onOrderPaid(products);
                    respond(RES_OK);

                    double total = 0.0;
                    for(Product p : products){
                        total += p.getPrice();
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
                                new Exception("Tried to charge a non-existent billing account").printStackTrace();
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

                    String name = readString();
                    int num_of_people = readInt();
                    int priority = readInt();

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
                     * Respond with ok or failed
                     */

                    long table_id = readLong();

                    Table table = Table.getTableById(table_id);
                    if(table==null){
                        new Exception("Tried to setReserved a non-existent table").printStackTrace();
                        return true;
                    }

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

                    long employee_id = readLong();
                    Employee empl = Employee.getEmployeeById(employee_id);

                    if(empl==null){
                        new Exception("Tried to edit non-existent employee").printStackTrace();
                        return true;
                    }

                    Bundle bundle = new Bundle();
                    bundle.put("is_new", readBoolean());

                    respond( empl.editData(bundle) ? RES_OK : RES_FAIL);

                    break;
                }

                case REQ_EDIT_TOPOLOGY:{
                    /*
                     * Format:
                     * no idea
                     *
                     * Respond with ok or failed
                     */

                    Bundle bundle = new Bundle();
                    // Fill bundle with data from sock_out

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
                     *
                     * Respond with ok or fail
                     */

                    long product_id = readLong();

                    Product product = Product.getProductById(product_id);
                    if(product==null){
                        new Exception("Tried to edit non-existent product").printStackTrace();
                        return true;
                    }

                    Bundle bundle = new Bundle();
                    bundle.put("price", readDouble());
                    bundle.put("description", readString());

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
                        new Exception("Tried to get offer for non-existent table").printStackTrace();
                        return true;
                    }

                    long client_id = table.getClient();
                    if(client_id != -1){
                        Product offer = Product.getOfferForClient(client_id);
                        if(offer!=null){
                            respond(Long.toString(offer.getId()));
                            break;
                        }
                    }

                    respond(RES_FAIL);

                    break;
                }

                default:
                    respond("STATUS 499 Disappointed: The server has received your request but thinks you can do better.");
                    //return true;

            }
        }catch(IOException | NumberFormatException e){
            e.printStackTrace();
            return true;
        }

        return false;
    }
}
