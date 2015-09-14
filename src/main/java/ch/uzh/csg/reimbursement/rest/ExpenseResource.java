package ch.uzh.csg.reimbursement.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.dto.AccessRights;
import ch.uzh.csg.reimbursement.dto.CommentDto;
import ch.uzh.csg.reimbursement.dto.CreateExpenseDto;
import ch.uzh.csg.reimbursement.dto.ExpenseDto;
import ch.uzh.csg.reimbursement.dto.ExpenseItemDto;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.model.ExpenseItemAttachment;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.service.CommentService;
import ch.uzh.csg.reimbursement.service.ExpenseItemService;
import ch.uzh.csg.reimbursement.service.ExpenseService;
import ch.uzh.csg.reimbursement.view.View;

import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/expenses")
@PreAuthorize("hasRole('USER')")
@Api(value = "Expense", description = "Authorized access required, only for users")
public class ExpenseResource {

	@Autowired
	private ExpenseService expenseService;

	@Autowired
	private ExpenseItemService expenseItemService;

	@Autowired
	private CommentService commentService;

	@JsonView(View.SummaryWithUid.class)
	@RequestMapping(method = POST)
	@ApiOperation(value = "Creates a new expense for currently logged in user")
	@ResponseStatus(CREATED)
	public Expense createExpense(@RequestBody CreateExpenseDto dto) {
		return expenseService.create(dto);
	}

	@JsonView(View.DashboardSummary.class)
	@RequestMapping(method = GET)
	@ApiOperation(value = "Find all expenses for the currently logged in user")
	public Set<Expense> getExpenses() {

		return expenseService.findAllByCurrentUser();
	}

	@JsonView(View.Summary.class)
	@RequestMapping(value = "/{expense-uid}", method = GET)
	@ApiOperation(value = "Find expense by uid")
	@ResponseStatus(OK)
	public Expense getExpenseByUid(@PathVariable("expense-uid") String uid) {
		return expenseService.findByUid(uid);
	}

	@RequestMapping(value = "/{expense-uid}", method = PUT)
	@ApiOperation(value = "Update the expense with the given uid.")
	@ResponseStatus(OK)
	public void updateExpense(@PathVariable("expense-uid") String uid, @RequestBody ExpenseDto dto) {
		expenseService.updateExpense(uid, dto);
	}

	@RequestMapping(value = "/{expense-uid}", method = DELETE)
	@ApiOperation(value = "Delete the expense with the given uid", notes = "Delete the expense with the given uid.")
	@ResponseStatus(OK)
	public void deleteExpense(@PathVariable("expense-uid") String uid) {
		expenseService.delete(uid);
	}

	@PreAuthorize("hasRole('PROF')")
	@RequestMapping(value = "/{expense-uid}/assign-to-finance-admin", method = PUT)
	@ApiOperation(value = "Assign the expense with the given uid to the finance admin.")
	@ResponseStatus(OK)
	public void assignExpenseToFinanceAdmin(@PathVariable("expense-uid") String uid) {
		expenseService.assignExpenseToFinanceAdmin(uid);
	}

	@RequestMapping(value = "/{expense-uid}/assign-to-prof", method = PUT)
	@ApiOperation(value = "Assign the expense with the given uid to the manager.")
	@ResponseStatus(OK)
	public void assignExpenseToProf(@PathVariable("expense-uid") String uid) {
		expenseService.assignExpenseToProf(uid);
	}

	@PreAuthorize("hasAnyRole('PROF', 'FINANCE_ADMIN')")
	@RequestMapping(value = "/{expense-uid}/reject", method = PUT)
	@ApiOperation(value = "Decline the expense with the given.")
	@ResponseStatus(OK)
	public void rejectExpense(@PathVariable("expense-uid") String uid, @RequestBody CommentDto dto) {
		expenseService.rejectExpense(uid, dto);
	}

	@RequestMapping(value = "/{expense-uid}/access-rights", method = GET)
	@ApiOperation(value = "Update the expense with the given uid.")
	@ResponseStatus(OK)
	public AccessRights getPermission(@PathVariable("expense-uid") String uid) {
		return expenseService.getAccessRights(uid);
	}

	@RequestMapping(value = "/{expense-uid}/expense-items", method = GET)
	@ApiOperation(value = "Find all expense-items of an expense for the currently logged in user", notes= "yyyy-MM-dd'T'HH:mm:ss.SSSZ, yyyy-MM-dd'T'HH:mm:ss.SSS'Z', EEE, dd MMM yyyy HH:mm:ss zzz, yyyy-MM-dd")
	public Set<ExpenseItem> getAllExpenseItems(@PathVariable ("expense-uid") String uid) {
		return expenseItemService.findAllExpenseItemsByExpenseUid(uid);
	}

	@JsonView(View.SummaryWithUid.class)
	@RequestMapping(value = "/{expense-uid}/expense-items", method = POST)
	@ApiOperation(value = "Create new expenseItem", notes = "Creates a new expenseItem for the specified expense.")
	@ResponseStatus(CREATED)
	public ExpenseItem createExpenseItem(@PathVariable("expense-uid") String uid, @RequestBody ExpenseItemDto dto) {
		return expenseItemService.create(uid, dto);
	}

	@RequestMapping(value = "/expense-items/{expense-item-uid}", method = GET)
	@ApiOperation(value = "Get the expenseItem with the given uid", notes = "Gets the expenseItem with the given uid.")
	public ExpenseItem getExpenseItem(@PathVariable("expense-item-uid") String uid) {
		return expenseItemService.findByUid(uid);
	}

	@RequestMapping(value = "/expense-items/{expense-item-uid}", method = PUT)
	@ApiOperation(value = "Update the expenseItem with the given uid", notes = "Updates the expenseItem with the given uid.")
	@ResponseStatus(OK)
	public void updateExpenseItem(@PathVariable("expense-item-uid") String uid, @RequestBody ExpenseItemDto dto) {
		expenseItemService.updateExpenseItem(uid, dto);
	}

	@RequestMapping(value = "/expense-items/{expense-item-uid}", method = DELETE)
	@ApiOperation(value = "Delete the expenseItem with the given uid", notes = "Delete the expenseItem with the given uid.")
	@ResponseStatus(OK)
	public void deleteExpenseItem(@PathVariable("expense-item-uid") String uid) {
		expenseItemService.delete(uid);
	}

	@RequestMapping(value = "/expense-items/{expense-item-uid}/attachments", method = GET)
	@ApiOperation(value = "Get a certain expenseItemAttachment", notes = "")
	@ResponseStatus(OK)
	public ExpenseItemAttachment getExpenseItemAttachment(@PathVariable ("expense-item-uid") String uid ) {
		return  expenseItemService.getExpenseItemAttachment(uid);
	}

	@JsonView(View.SummaryWithUid.class)
	@RequestMapping(value = "/expense-items/{expense-item-uid}/attachments", method = POST)
	@ApiOperation(value = "Upload a new expenseItemAttachment", notes = "")
	@ResponseStatus(CREATED)
	public ExpenseItemAttachment uploadExpenseItemAttachment(@PathVariable ("expense-item-uid") String uid,@RequestParam("file") MultipartFile file ) {
		return expenseItemService.setAttachment(uid, file);
	}

	@RequestMapping(value = "/expense-items/{expense-item-uid}/attachments/token", method = POST)
	@ApiOperation(value = "Create a new expenseItemAttachment token for mobile access")
	public Token createExpenseItemAttachmentMobileToken(@PathVariable ("expense-item-uid") String uid) {
		return expenseItemService.createExpenseItemAttachmentMobileToken(uid);
		//TODO The attachmnet service does sometimes not include the content - occurs only at first popup open...
	}

	@PreAuthorize("hasAnyRole('PROF', 'FINANCE_ADMIN')")
	@JsonView(View.DashboardSummary.class)
	@RequestMapping(value = "/review-expenses", method = GET)
	@ApiOperation(value = "Find all review expenses for the currently logged in user.")
	public Set<Expense> getReviewExpenses() {
		return expenseService.getAllReviewExpenses();
	}

	@RequestMapping(value = "/user/{user-uid}", method = GET)
	@ApiOperation(value = "Find all expenses for a given user.", notes = "Finds all expenses that were created by the user.")
	public Set<Expense> getAllExpenses(@PathVariable ("user-uid") String uid) {
		return expenseService.findAllByUser(uid);
	}
}