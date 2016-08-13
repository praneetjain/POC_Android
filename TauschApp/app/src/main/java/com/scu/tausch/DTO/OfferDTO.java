package com.scu.tausch.DTO;

import android.graphics.Bitmap;

/**
 * Created by Pratyusha on 1/22/2016.
 */
public class OfferDTO {

    private String offerId;
    private String postedBy;
    private String categoryId;
    private String offerTitle;
    private String offerDescription;
    private String cityId;
    private String zip;
    private String condition;
    private String rentalType;
    private double price;
    private Bitmap image_one;
    private Bitmap image_two;
    private Bitmap image_three;
    private Bitmap image_four;
    private Bitmap image_five;
    private String offeror;
    private boolean offerStatus;
    private String sortCriteriaSelected;

    public OfferDTO(){

    }

    public OfferDTO(String offerId, String postedBy, String categoryId, String offerTitle, String offerDescription,
                    String cityId, String zip, String condition, String rentalType, double price, Bitmap image_one, String offeror, boolean offerStatus) {
        this.offerId = offerId;
        this.postedBy = postedBy;
        this.categoryId = categoryId;
        this.offerTitle = offerTitle;
        this.offerDescription = offerDescription;
        this.cityId = cityId;
        this.zip = zip;
        this.condition = condition;
        this.rentalType = rentalType;
        this.price = price;
        this.image_one = image_one;
        this.offeror = offeror;
        this.offerStatus = offerStatus;
    }

    public String getOfferorName() {
        return offeror;
    }

    public void setOfferorName(String offeror) {
        this.offeror = offeror;
    }
    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Bitmap getImage_one() {
        return image_one;
    }

    public void setImage_one(Bitmap image_one) {
        this.image_one = image_one;
    }

    public Bitmap getImage_two() {
        return image_two;
    }

    public void setImage_two(Bitmap image_two) {
        this.image_two = image_two;
    }

    public Bitmap getImage_three() {
        return image_three;
    }

    public void setImage_three(Bitmap image_three) {
        this.image_three = image_three;
    }

    public Bitmap getImage_four() {
        return image_four;
    }

    public void setImage_four(Bitmap image_four) {
        this.image_four = image_four;
    }

    public Bitmap getImage_five() {
        return image_five;
    }

    public void setImage_five(Bitmap image_five) {
        this.image_five = image_five;
    }

    public boolean isOfferStatus() {
        return offerStatus;
    }

    public void setOfferStatus(boolean offerStatus) {
        this.offerStatus = offerStatus;
    }

    public String getRentalType() {
        return rentalType;
    }

    public void setRentalType(String rentalType) {
        this.rentalType = rentalType;
    }

    public String getSortCriteriaSelected() {
        return sortCriteriaSelected;
    }

    public void setSortCriteriaSelected(String sortCriteriaSelected) {
        this.sortCriteriaSelected = sortCriteriaSelected;
    }
}
