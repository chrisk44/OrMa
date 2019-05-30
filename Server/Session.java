import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;

public class Session{
    // Responses
    static final String RES_OK = "ok",
                        RES_FAIL = "fail";

    // Requests
    static final String REQ_LOGIN = "login",
                        REQ_NEW_ORDER = "new_order",
                        REQ_EXIT = "exit",
                        REQ_TB_CALL = "tb_call",                /* Sequence 1, 3, 10 */
                        REQ_EDIT_ORDER = "edit_order",          /* Sequence 1 */
                        REQ_SEND_ORDER = "send_order",          /* Sequence 2 */
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
                case "":{
                    break;
                }

                case REQ_EXIT:{
                    return true;
                }

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
                            long prepAreaId = Long.parseLong(data[1]);
                            String pass_hash = data[2];
                            // Assume we verify this with the database, also get prepArea_id, is_new and id
                            long pae_id = 5;

                            PrepAreaEmployee pae = new PrepAreaEmployee(pae_id, false);
                            device = new PrepAreaDevice(pae, PrepArea.getPrepAreaById(prepAreaId), socket.getInetAddress());
                            pae.setDevice((PrepAreaDevice)device);
                            respond(Long.toString(pae_id));
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
                     * Can only be called by a TableButton or a Waiter (after rejection of call)
                     *
                     * Format:
                     * table_id\n
                     *
                     * Respond with ok
                     */

                    if(!(this.device instanceof TableButton) && !(this.device.getEmployee() instanceof Waiter)){
                        new Exception("tb_call can only be called by a TableButton or a Waiter").printStackTrace();
                        return true;
                    }

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
                     * Can only be called by a Waiter
                     *
                     * Format:
                     * table_id\n
                     *
                     * Create a new order and respond with its id, or failed if the table already has an order
                     */

                    if(!(this.device.getEmployee() instanceof Waiter)){
                        new Exception("new_order can only be called by a Waiter").printStackTrace();
                        return true;
                    }

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
                     * Can only be called by a Waiter
                     *
                     * Format:
                     * order_id\n
                     * num_of_products\n
                     * action;product_id
                     * action;product_id
                     * ...
                     *
                     * Respond with ok or failed
                     */

                    if(!(this.device.getEmployee() instanceof Waiter)){
                        new Exception("edit_order can only be called by a Waiter").printStackTrace();
                        return true;
                    }

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
                     * Can only be called by a Waiter
                     *
                     * Format:
                     * order_id\n
                     *
                     * Respond with ok
                     */

                    if(!(this.device.getEmployee() instanceof Waiter)){
                        new Exception("send_order can only be called by a Waiter").printStackTrace();
                        return true;
                    }

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
                     * Can only be called by a PrepArea
                     *
                     * Format:
                     * nothing
                     *
                     * Respond with list of orders:
                     * num_of_orders\n
                     * order_id\n
                     * order_id\n
                     * ...
                     */

                    if(!(this.device instanceof PrepAreaDevice)){
                        new Exception("auto_assign can only be called by a PrepAreaDevice").printStackTrace();
                        return true;
                    }

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
                     * Can only be called by a PrepArea
                     *
                     * Format:
                     * num_of_orders\n
                     * order_id\n
                     * order_id\n
                     * ...
                     *
                     * Respond with ok
                     */

                    if(!(this.device instanceof PrepAreaDevice)){
                        new Exception("assign_orders can only be called by a PrepAreaDevice").printStackTrace();
                        return true;
                    }

                    PrepArea currentPA = ((PrepAreaDevice)(this.device)).getPrepArea();
                    int num_of_orders = readInt();
                    for(int i=0; i<num_of_orders; i++){
                        Order o = Order.getOrderById(readLong());

                        if(o==null){
                            new Exception("Tried to assign non-existent order").printStackTrace();
                            return true;
                        }

                        if(o.getAssignedArea()==null){
                            new Exception("Tried to assign non-sent order").printStackTrace();
                            return true;
                        }

                        o.assignOrder(currentPA);
                    }

                    respond(RES_OK);

                    break;
                }

                case REQ_MARK_READY:{
                    /*
                     * Can only be called by a PrepArea
                     *
                     * Format:
                     * nothing, mark ready any order assigned this prepArea
                     *
                     * Respond with ok
                     */

                    if(!(this.device instanceof PrepAreaDevice)){
                        new Exception("mark_ready can only be called by a PrepAreaDevice").printStackTrace();
                        return true;
                    }

                    new Thread(() -> {
                        PrepArea currentPA = ((PrepAreaDevice)(this.device)).getPrepArea();
                        for(Order o : currentPA.getAssignedOrders()){
                            o.setReady();
                        }

                        currentPA.clearAssignedOrders();
                    }).start();

                    respond(RES_OK);

                    break;
                }

                case REQ_PAY_ORDER:{
                    /*
                     * Can only be called by a Waiter
                     *
                     * Format:
                     * table_id\n
                     * payment_type\n    <-- 0:cash, -1:pos, 1+:billing_account_id
                     * num_of_products\n
                     * product_id\n
                     * product_id\n
                     * ...
                     *
                     * Respond with ok when done
                     */

                    if(!(this.device.getEmployee() instanceof Waiter)){
                        new Exception("pay_order can only be called by a Waiter").printStackTrace();
                        return true;
                    }

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
                     * Can only be called by a PR
                     *
                     * Format:
                     * name\n
                     * num_of_people\n
                     * priority\n
                     *
                     * Respond with ok
                     */

                    if(!(this.device.getEmployee() instanceof PR)){
                        new Exception("add_to_wl can only be called by a PR").printStackTrace();
                        return true;
                    }

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
                     * Can only be called by a Waiter
                     *
                     * Format:
                     * table_id\n
                     *
                     * Respond with product_id or fail
                     */

                    if(!(this.device.getEmployee() instanceof Waiter)){
                        new Exception("get_offer can only be called by a Waiter").printStackTrace();
                        return true;
                    }

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

                case "print":{
                    respond("1. Tables");
                    respond("2. PrepAreas");
                    respond("3. Orders");
                    respond("4. Waiting List");

                    int choice = Integer.parseInt(readString());
                    switch(choice){
                        case 1:
                            for(Table t : Table.allTables)
                                respond(t.toString());

                            break;

                        case 2:
                            for(PrepArea pa : PrepArea.allPrepAreas)
                                respond(pa.toString());

                            break;

                        case 3:
                            for(Order o : Order.allOrders)
                                respond(o.toString());

                            break;

                        case 4:
                            for(WaitingGroup wg : WaitingGroup.waitingList)
                                respond(wg.toString());
                    }
                    break;
                }

                default:{
                    String encodedString = "U1RBVFVTIDQ5OSBEaXNhcHBvaW50ZWQ6IFR" +
                            "oZSBzZXJ2ZXIgaGFzIHJlY2VpdmVkIHlvdXIgcmVxdWVzdCBid" +
                            "XQgdGhpbmtzIHlvdSBjYW4gZG8gYmV0dGVyLgpUaGlzIGlzIGp" +
                            "1c3QgbGF6eSB3cml0aW5nClRoYXQncyBub3QgaG93IGl0IHdvc" +
                            "mtzLiBUaGF0J3Mgbm90IGhvdyBhbnkgb2YgaXQgd29ya3MKV2h" +
                            "hdD8KVHJ5IGFnYWluCklzIHRoaXMgdGhlIGJlc3QgeW91IGNhb" +
                            "iBkbz8KWW91ciBwYXJlbnRzIG11c3QgYmUgcHJvdWQKWW91IGF" +
                            "yZSBhIGRpc2FwcG9pbnRtZW50ClNvIG1hbnkgZWxlY3Ryb25zI" +
                            "GdvdCBpbmNvbnZlbmllbmNlZCBmb3IgdGhpcwpIdWg/PwpZb3U" +
                            "ncmUgYSBqb2tlCllvdSdyZSBub3QgdmVyeSBnb29kIGF0IHRoa" +
                            "XMKVGhhdCdzIGl0PyBUaGF0J3MgeW91ciBicmFpbiB3b3JraW5" +
                            "nIGF0IGZ1bGwgY2FwYWNpdHk/CnNyc2x5ClRoaXMgaXMgd2hhd" +
                            "CBoYXBwZW5zIHdoZW4geW91IGRvbid0IHJlYWQgdGhlIGRvY3V" +
                            "tZW50YXRpb24KV2h5IGFyZSB5b3UgZXZlbiB1c2luZyB5b3VyI" +
                            "GtleWJvLSB0aGlzIHNob3VsZCBiZSBkb25lIGJ5IHNvZnR3YXJ" +
                            "lLCB5b3UgYXJlIG5vdCBzbWFydCBlbm91Z2ggZm9yIHRoaXMKQ" +
                            "XJlIHlvdSBldmVuIHRyeWluZz8=";

                    String[] insults = new String(Base64.getDecoder().decode(encodedString)).split("\n");
                    respond(insults[Main.r.nextInt(insults.length)]);
                }
            }
        }catch(IOException | NumberFormatException e){
            e.printStackTrace();
            return true;
        }

        return false;
    }
}
